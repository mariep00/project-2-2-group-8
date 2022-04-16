package gui.gamescreen;

import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.controller.ControllerSurveillanceGUI;
import javafx.stage.Stage;

public class GameScreenSurveillance extends GameScreen {
    private final ControllerSurveillanceGUI controllerSurveillanceGUI;

    public GameScreenSurveillance(ScenarioMap scenarioMap) {
        super(scenarioMap);
        this.controllerSurveillanceGUI = new ControllerSurveillanceGUI(scenarioMap, new EndingSurveillance(), this);
    }

    @Override
    public void start(Stage stage) {
        super.start(stage);
        controllerSurveillanceGUI.init();
    }

    @Override
    protected Tile getInitialFloorTile() { return new Tile(new TileImage(imageContainer.getFloor())); }

    @Override
    protected ControllerSurveillanceGUI getController() { return controllerSurveillanceGUI; }
}
