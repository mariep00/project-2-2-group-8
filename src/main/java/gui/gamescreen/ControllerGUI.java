package gui.gamescreen;

import gamelogic.controller.Controller;
import gamelogic.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.maps.ScenarioMap;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllerGUI extends Controller {
    private final GameScreen GAME_SCREEN;
    private final ConcurrentLinkedQueue<Runnable> guiTasksQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean executeNextGuiTask = new AtomicBoolean(true);
    private final AtomicBoolean performControllerTick = new AtomicBoolean(true);
    private final AtomicBoolean gamePaused = new AtomicBoolean(false);
    private final AtomicBoolean runSimulation = new AtomicBoolean(false);
    private final AtomicInteger simulationDelay = new AtomicInteger();
    private final Thread updateGameLogicThread;
    public ControllerGUI(ScenarioMap scMap, GameScreen gameScreen) {
        super(scMap);
        this.GAME_SCREEN = gameScreen;

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
                    tick();
                }
            }
        });
        updateGameLogicThread.setDaemon(true);
        updateGameLogicThread.setUncaughtExceptionHandler(h);
    }

    public void runSimulation() {
        runSimulation.set(true);
        if (!updateGameLogicThread.isAlive()) updateGameLogicThread.start();
    }
    public void stopSimulation() {
        runSimulation.set(false);
    }

    public void pauseThreads() { gamePaused.set(true); }
    public void continueThreads() { gamePaused.set(false); }


    @Override
    protected void spawnAgents() {
        super.spawnAgents();
        for (int i = 0; i < agentsGuards.length; i++) {
            GAME_SCREEN.spawnAgent(i, agentPositions[i]);
        }
    }

    @Override
    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        Vector2D oldPos = agentPositions[agentIndex];
        super.updateAgentPosition(agentIndex, pos);
        guiTasksQueue.add(() -> Platform.runLater(() -> GAME_SCREEN.moveAgent(executeNextGuiTask, agentIndex,oldPos,agentPositions[agentIndex])));
    }

    @Override
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        super.updateAgentOrientation(agentIndex, orientationToAdd);
        guiTasksQueue.add(() -> Platform.runLater(() -> GAME_SCREEN.moveAgent(executeNextGuiTask, agentIndex, agentPositions[agentIndex], agentPositions[agentIndex])));
    }

    @Override
    protected boolean updateProgress(List<Vector2D> vectors, int agentIndex) {
        boolean toReturn = super.updateProgress(vectors, agentIndex);
        List<Vector2D> absPos = convertRelativeCurrentPosToAbsolute(vectors, agentIndex);
        guiTasksQueue.add(() -> Platform.runLater(() -> GAME_SCREEN.setProgress(executeNextGuiTask,endingExplorationMap.getCurrentTilesExplored(), endingExplorationMap.getTotalTilesToExplore())));
        guiTasksQueue.add(() -> Platform.runLater(() -> GAME_SCREEN.setToExplored(executeNextGuiTask, absPos)));
        return toReturn;
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < agentsGuards.length; i++) {
            int finalI = i;
            Vector2D pos = agentPositions[i];
            updateAgentVision(finalI, calculateFOV(finalI, pos));
        }
    }

    @Override
    protected void updateAgent(int agentIndex, int task) {
        super.updateAgent(agentIndex, task);
        List<Vector2D> positionsInVision = calculateFOV(agentIndex, agentPositions[agentIndex]);
        updateAgentVision(agentIndex, positionsInVision);
        updateProgress(positionsInVision, agentIndex);
    }

    public Agent getAgent(int index) { return agentsGuards[index]; }

    private void updateAgentVision(int agentIndex, List<Vector2D> positionsInVision) {
        List<Vector2D> absPos = convertRelativeCurrentPosToAbsolute(positionsInVision, agentIndex);
        guiTasksQueue.add(() -> Platform.runLater(() -> GAME_SCREEN.updateVision(executeNextGuiTask, agentIndex, absPos)));
    }

    public void hideVision(int agentIndex) {
        guiTasksQueue.add(() -> Platform.runLater(() -> GAME_SCREEN.removeVision(executeNextGuiTask, agentIndex, convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPositions[agentIndex]), agentIndex))));
    }
    public void showVision(int agentIndex) {
        guiTasksQueue.add(() -> Platform.runLater(() -> GAME_SCREEN.showVision(executeNextGuiTask, convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPositions[agentIndex]), agentIndex))));
    }

    public AtomicBoolean getRunSimulation() { return runSimulation; }
    public void setSimulationDelay(int val) { this.simulationDelay.set(val); }
}
