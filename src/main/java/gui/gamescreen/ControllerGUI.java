package gui.gamescreen;

import controller.Controller;
import controller.Vector2D;
import controller.agent.Agent;
import controller.maps.ScenarioMap;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerGUI extends Controller {
    private final GameScreen GAME_SCREEN;
    private final LinkedList<Runnable> guiTasksToExecute = new LinkedList<>();
    private final AtomicBoolean executeNextGuiTask = new AtomicBoolean(true);
    private final AtomicBoolean performControllerTick = new AtomicBoolean(true);
    private final AtomicBoolean gamePaused = new AtomicBoolean(false);
    private final AtomicBoolean runSimulation = new AtomicBoolean(false);
    private Thread updateGameLogicThread;
    public ControllerGUI(ScenarioMap scMap, GameScreen gameScreen) {
        super(scMap);
        this.GAME_SCREEN = gameScreen;

        final Thread updateGuiThread = new Thread(() -> {
            while (true) {
                if (executeNextGuiTask.get() && guiTasksToExecute.size() > 0 && !gamePaused.get()) {
                    executeNextGuiTask.set(false);
                    guiTasksToExecute.removeFirst().run();
                }
                else if (guiTasksToExecute.size() == 0) performControllerTick.set(true);
            }
        });
        //updateGuiThread.setDaemon(true);
        updateGuiThread.start();
    }

    public void runSimulation() {
        runSimulation.set(true);
        updateGameLogicThread = new Thread(() -> {
            while (true) {
                if (runSimulation.get() && performControllerTick.get() && !gamePaused.get()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500); // Slow down the simulation
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    performControllerTick.set(false);
                    tick();
                }
            }
        });
        //updateGameLogicThread.setDaemon(true);
        updateGameLogicThread.start();
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
        guiTasksToExecute.add(() -> Platform.runLater(() -> GAME_SCREEN.moveAgent(executeNextGuiTask, agentIndex,oldPos,agentPositions[agentIndex])));
    }

    @Override
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        super.updateAgentOrientation(agentIndex, orientationToAdd);
        guiTasksToExecute.add(() -> Platform.runLater(() -> GAME_SCREEN.moveAgent(executeNextGuiTask, agentIndex, agentPositions[agentIndex], agentPositions[agentIndex])));
    }

    @Override
    protected boolean updateProgress(Vector2D vector, int agentIndex) {
        boolean toReturn = super.updateProgress(vector, agentIndex);
        guiTasksToExecute.add(() -> Platform.runLater(() -> GAME_SCREEN.setProgress(executeNextGuiTask,endingExplorationMap.getCurrentTilesExplored(), endingExplorationMap.getTotalTilesToExplore())));
        guiTasksToExecute.add(() -> Platform.runLater(() -> GAME_SCREEN.setToExplored(executeNextGuiTask, convertRelativeCurrentPosToAbsolute(vector, agentIndex))));
        return toReturn;
    }

    @Override
    public void init() {
        super.init();
        /*ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        for (int i = 0; i < agentsGuards.length; i++) {
            int finalI = i;
            threadPool.submit(() -> updateAgentVision(finalI, calculateFOV(finalI, agentPositions[finalI])));
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void updateAgent(int agentIndex, int task) {
        super.updateAgent(agentIndex, task);
        ArrayList<Vector2D> positionsInVision = calculateFOV(agentIndex, agentPositions[agentIndex]);
        //guiTasksToExecute.add(() -> Platform.runLater(() -> updateAgentVision(agentIndex, positionsInVision)));
        for (Vector2D vector : positionsInVision) {
            //guiTasksToExecute.add(() -> Platform.runLater(() -> updateProgress(vector, agentIndex)));
            super.updateProgress(vector, agentIndex);
            guiTasksToExecute.add(() -> Platform.runLater(() -> GAME_SCREEN.setProgress(executeNextGuiTask,endingExplorationMap.getCurrentTilesExplored(), endingExplorationMap.getTotalTilesToExplore())));
            guiTasksToExecute.add(() -> Platform.runLater(() -> GAME_SCREEN.setToExplored(executeNextGuiTask, convertRelativeCurrentPosToAbsolute(vector, agentIndex))));

        }
    }

    public Agent getAgent(int index) { return agentsGuards[index]; }

    private void updateAgentVision(int agentIndex, ArrayList<Vector2D> positionsInVision) {
        /*ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        threadPool.submit(() -> GAME_SCREEN.updateVision(agentIndex, convertRelativeCurrentPosToAbsolute(positionsInVision, agentIndex)));
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void hideVision(int agentIndex) {
        /*ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        threadPool.submit(() -> GAME_SCREEN.removeVision(agentIndex, convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPositions[agentIndex]), agentIndex)));
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
    public void showVision(int agentIndex) {
        /*ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        threadPool.submit(() -> GAME_SCREEN.showVision(convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPositions[agentIndex]), agentIndex)));
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
