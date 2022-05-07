package gui.gamescreen.controller;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.GameScreen;
import gui.gamescreen.GameScreenExploration;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerExplorationGUI extends ControllerExploration implements ControllerGUIInterface {
    private final GameScreen gameScreen;
    private final ControllerGUI controllerGUI;
    public ControllerExplorationGUI(ScenarioMap scenarioMap, EndingExploration endingCondition, GameScreen gameScreen, TaskContainer taskContainer) {
        super(scenarioMap, endingCondition, taskContainer);
        this.gameScreen = gameScreen;
        this.controllerGUI = new ControllerGUI(this, gameScreen);
    }

    private void updateExplored() {
        for (List<Vector2D> vision : nextState.getVisions()) {
            controllerGUI.addGuiRunnableToQueue(() -> ((GameScreenExploration) gameScreen).setToExplored(controllerGUI.getExecuteNextGuiTask(), vision));
        }
        controllerGUI.addGuiRunnableToQueue(() -> ((GameScreenExploration) gameScreen).setProgress(controllerGUI.getExecuteNextGuiTask(), ((EndingExploration) endingCondition).getCurrentTilesExplored(), ((EndingExploration) endingCondition).getTotalTilesToExplore()));
    }

    @Override
    public void end() {
        controllerGUI.killLogicThread();
        controllerGUI.guiTasksQueue.add(() -> {
            controllerGUI.killGuiThread();
            Platform.runLater(gameScreen::endScreen);
        });
    }
    @Override
    public void init() {
        super.init();
        controllerGUI.init();
        updateExplored();
    }

    @Override
    public void updateGui() {
        controllerGUI.updateGui();
        updateExplored();
    }

    @Override
    public void hideVision(int agentIndex) {
        controllerGUI.hideVision(agentIndex);
    }

    @Override
    public void showVision(int agentIndex) {
        controllerGUI.showVision(agentIndex);
    }

    @Override
    public void setSimulationDelay(int val) {
        controllerGUI.setSimulationDelay(val);
    }

    @Override
    public void runSimulation() {
        controllerGUI.runSimulation();
    }

    @Override
    public void stopSimulation() {
        controllerGUI.stopSimulation();
    }

    @Override
    public void pauseThreads() {
        controllerGUI.pauseThreads();
    }

    @Override
    public void continueThreads() {
        controllerGUI.continueThreads();
    }

    @Override
    public AtomicBoolean getRunSimulation() {
        return controllerGUI.getRunSimulation();
    }

    // TODO Update the progress of the exploration in the gui
}
