package gui.gamescreen.controller;

import datastructures.Vector2D;
import gamelogic.controller.Controller;
import gui.gamescreen.AgentType;
import gui.gamescreen.GameScreen;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllerGUI implements ControllerGUIInterface {
    private final Controller controller;
    private final GameScreen gameScreen;
    public final ConcurrentLinkedQueue<Runnable> guiTasksQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean executeNextGuiTask = new AtomicBoolean(true);
    private final AtomicBoolean performControllerTick = new AtomicBoolean(true);
    private final AtomicBoolean gamePaused = new AtomicBoolean(false);
    private final AtomicBoolean runSimulation = new AtomicBoolean(false);
    private final AtomicInteger simulationDelay = new AtomicInteger();
    private Thread updateGameLogicThread;

    private AtomicBoolean logicThreadKilled; // set to true when end method is called
    private AtomicBoolean guiThreadKilled;

    public ControllerGUI(Controller controller, GameScreen gameScreen) {
        this.controller = controller;
        this.gameScreen = gameScreen;
        this.logicThreadKilled = new AtomicBoolean(false);
        this.guiThreadKilled = new AtomicBoolean(false);
    }

    public void init() {
        Thread.UncaughtExceptionHandler h = (th, ex) -> System.out.println("Uncaught exception: " + ex);

        final Thread updateGuiThread = new Thread(() -> {
            while (!guiThreadKilled.get()) {
                if (executeNextGuiTask.get() && !guiTasksQueue.isEmpty() && !gamePaused.get()) {
                    executeNextGuiTask.set(false);
                    guiTasksQueue.remove().run();
                    if (guiTasksQueue.isEmpty()) performControllerTick.set(true);
                }
            }
        });
        updateGuiThread.setDaemon(true);
        updateGuiThread.setUncaughtExceptionHandler(h);
        updateGuiThread.start();

        updateGameLogicThread = new Thread(() -> {
            while (!logicThreadKilled.get()) {
                if (runSimulation.get() && performControllerTick.get() && !gamePaused.get()) {
                    performControllerTick.set(false);
                    try {
                        TimeUnit.MILLISECONDS.sleep(simulationDelay.get()); // Slow down the simulation
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    controller.tick(true);
                }
            }
        });
        updateGameLogicThread.setDaemon(true);
        updateGameLogicThread.setUncaughtExceptionHandler(h);

        for (int i = 0; i < controller.getNumberOfGuards(); i++) {
            gameScreen.spawnAgent(i, controller.getCurrentState().getAgentPosition(i), AgentType.GUARD);
        }
        for (int i = controller.getNumberOfGuards(); i < controller.getNumberOfGuards()+controller.getNumberOfIntruders(); i++) {
            gameScreen.spawnAgent(i, controller.getCurrentState().getAgentPosition(i), AgentType.INTRUDER);
        }
    }

    public void updateGui() {
        for (int i = 0; i < controller.getNumberOfGuards()+controller.getNumberOfIntruders(); i++) {
            if (controller.getAgent(i) != null) {
                AgentType agentType;
                if (i < controller.getNumberOfGuards()) agentType = AgentType.GUARD;
                else agentType = AgentType.INTRUDER;

                int finalI = i;
                Vector2D currentPos = controller.getCurrentState().getAgentPosition(finalI);
                Vector2D nextPos = controller.getNextState().getAgentPosition(finalI);
                List<Vector2D> currentVision = getVisionToRemove(finalI, controller.getCurrentState().getVision(finalI));
                List<Vector2D> nextVision = controller.getNextState().getVision(finalI);

                guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.moveAgent(executeNextGuiTask, finalI, currentPos, nextPos, agentType)));
                guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.updateVision(executeNextGuiTask, finalI, currentVision, nextVision)));
            }
        }
    }

    private List<Vector2D> getVisionToRemove(int agentIndex, List<Vector2D> visionToRemove) {
        ArrayList<Vector2D> visionToRemoveFinal = new ArrayList<>();
        for (Vector2D pos : visionToRemove) {
            boolean remove = true;
            outer:
            for (int i = 0; i < controller.getCurrentState().getVisions().length; i++) {
                if (i != agentIndex && gameScreen.getShowVision(i) && controller.getAgent(i) != null) {
                    List<Vector2D> others = controller.getNextState().getVision(i);
                    for (Vector2D posOther : others) {
                        if (posOther.equals(pos)) {
                            remove = false;
                            break outer;
                        }
                    }
                }
            }
            if (remove) visionToRemoveFinal.add(pos);
        }
        return visionToRemoveFinal;
    }

    @Override
    public void hideVision(int agentIndex) {
        List<Vector2D> visionToRemove = getVisionToRemove(agentIndex, controller.getCurrentState().getVision(agentIndex));
        guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.removeVision(executeNextGuiTask, visionToRemove)));
    }
    @Override
    public void showVision(int agentIndex) {
        List<Vector2D> visionToShow = controller.getCurrentState().getVision(agentIndex);
        guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.showVision(executeNextGuiTask, visionToShow)));
    }
    @Override
    public void setSimulationDelay(int val) { this.simulationDelay.set(val); }
    @Override
    public void runSimulation() {
        runSimulation.set(true);
        if (!updateGameLogicThread.isAlive()) updateGameLogicThread.start();
    }
    @Override
    public void stopSimulation() {
        runSimulation.set(false);
    }
    @Override
    public void pauseThreads() { gamePaused.set(true); }
    @Override
    public void continueThreads() { gamePaused.set(false); }
    @Override
    public AtomicBoolean getRunSimulation() { return runSimulation; }

    @Override
    public boolean doesAgentExist(int agentIndex) {
        return controller.getAgent(agentIndex) != null;
    }

    public AtomicBoolean getExecuteNextGuiTask() { return executeNextGuiTask; }
    public void addGuiRunnableToQueue(Runnable runnable) { guiTasksQueue.add(() -> Platform.runLater(runnable)); }
    public void killLogicThread(){ logicThreadKilled.set(true); }
    public void killGuiThread(){ guiThreadKilled.set(true); }
    public Controller getMainController() {
        return controller;
    }
}
