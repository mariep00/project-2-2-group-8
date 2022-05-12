package gui.gamescreen.controller;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.GameScreen;

import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerSurveillanceGUI extends ControllerSurveillance implements ControllerGUIInterface {
    private final GameScreen gameScreen;
    private final ControllerGUI controllerGUI;

    public ControllerSurveillanceGUI(ScenarioMap scenarioMap, EndingSurveillance endingCondition, GameScreen gameScreen, TaskContainer taskContainer) {
        super(scenarioMap, endingCondition, taskContainer);
        this.gameScreen = gameScreen;
        this.controllerGUI = new ControllerGUI(this, gameScreen);
    }

    @Override
    public void init() {
        super.init();
        controllerGUI.init();
    }

    @Override
    public void updateGui() {
        controllerGUI.updateGui();
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

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public ControllerGUI getControllerGUI() {
        return controllerGUI;
    }
}
