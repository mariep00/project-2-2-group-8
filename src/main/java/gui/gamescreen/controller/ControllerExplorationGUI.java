package gui.gamescreen.controller;

import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.GameScreen;

public class ControllerExplorationGUI extends ControllerExploration {
    private final ControllerGUI controllerGUI;
    public ControllerExplorationGUI(ScenarioMap scMap, GameScreen gameScreen, EndingExploration endingCondition) {
        super(scMap, endingCondition);
        this.controllerGUI = new ControllerGUI(gameScreen, this);
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

    // TODO Update the progress of the exploration in the gui
}
