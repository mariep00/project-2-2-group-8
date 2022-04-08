package gui.gamescreen.controller;

import gamelogic.agent.Agent;
import gamelogic.controller.Controller;
import gui.gamescreen.GameScreen;
import javafx.application.Platform;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllerGUI {
    private final GameScreen gameScreen;
    private final Controller controller;
    private final ConcurrentLinkedQueue<Runnable> guiTasksQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean executeNextGuiTask = new AtomicBoolean(true);
    private final AtomicBoolean performControllerTick = new AtomicBoolean(true);
    private final AtomicBoolean gamePaused = new AtomicBoolean(false);
    private final AtomicBoolean runSimulation = new AtomicBoolean(false);
    private final AtomicInteger simulationDelay = new AtomicInteger();
    private final Thread updateGameLogicThread;

    public ControllerGUI(GameScreen gameScreen, Controller controller) {
        this.gameScreen = gameScreen;
        this.controller = controller;

        Thread.UncaughtExceptionHandler h = (th, ex) -> System.out.println("Uncaught exception: " + ex);

        final Thread updateGuiThread = new Thread(() -> {
            while (true) {
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
            while (true) {
                if (runSimulation.get() && performControllerTick.get() && !gamePaused.get()) {
                    performControllerTick.set(false);
                    try {
                        TimeUnit.MILLISECONDS.sleep(simulationDelay.get()); // Slow down the simulation
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    controller.tick();
                }
            }
        });
        updateGameLogicThread.setDaemon(true);
        updateGameLogicThread.setUncaughtExceptionHandler(h);
    }

    public void init() {
        for (int i = 0; i < controller.getNumberOfGuards()+controller.getNumberOfIntruders(); i++) {
            gameScreen.spawnAgent(i, controller.getCurrentState().getAgentPosition(i));
            gameScreen.showVision(null, controller.getCurrentState().getVision(i));
        }
    }

    protected void updateGui() {
        performControllerTick.set(false);
        for (int i = 0; i < controller.getNumberOfGuards()+controller.getNumberOfIntruders(); i++) {
            int finalI = i;
            guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.moveAgent(executeNextGuiTask, finalI,controller.getCurrentState().getAgentPosition(finalI),controller.getNextState().getAgentPosition(finalI))));
            guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.updateVision(executeNextGuiTask, finalI, controller.getCurrentState().getVision(finalI), controller.getNextState().getVision(finalI))));
        }
        while (!performControllerTick.get()) {}
    }

    public void hideVision(int agentIndex) {
        guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.removeVision(executeNextGuiTask, agentIndex, controller.getCurrentState().getVision(agentIndex))));
    }
    public void showVision(int agentIndex) {
        guiTasksQueue.add(() -> Platform.runLater(() -> gameScreen.showVision(executeNextGuiTask, controller.getCurrentState().getVision(agentIndex))));
    }

    public AtomicBoolean getRunSimulation() { return runSimulation; }
    public void setSimulationDelay(int val) { this.simulationDelay.set(val); }

    public void runSimulation() {
        runSimulation.set(true);
        if (!updateGameLogicThread.isAlive()) updateGameLogicThread.start();
    }
    public void stopSimulation() {
        runSimulation.set(false);
    }

    public void pauseThreads() { gamePaused.set(true); }
    public void continueThreads() { gamePaused.set(false); }
    public Agent getAgent(int index) { return controller.getAgent(index); }
}
