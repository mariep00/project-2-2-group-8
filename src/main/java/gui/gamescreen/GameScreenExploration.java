package gui.gamescreen;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.controller.ControllerExplorationGUI;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameScreenExploration extends GameScreen {
    private final ControllerExplorationGUI controllerExplorationGUI;
    private ProgressBar progressBar;

    public GameScreenExploration(ScenarioMap scenarioMap) {
        super(scenarioMap);
        this.controllerExplorationGUI = new ControllerExplorationGUI(scenarioMap, new EndingExploration(scenarioMap), this, new TaskContainer(new ExplorationTaskFrontier()));
    }

    @Override
    public void start(Stage stage) {
        super.start(stage);
        controllerExplorationGUI.init();
    }

    @Override
    protected HBox loadInformationBar() {
        HBox barHBox = super.loadInformationBar();

        progressBar = new ProgressBar();
        progressBar.getProgressBar().setPrefWidth(400);
        progressBar.getProgressBar().setPrefHeight(35);

        barHBox.getChildren().add(0, progressBar);

        return barHBox;
    }

    public void setProgress(AtomicBoolean executeNextGuiTask, int numberOfTilesExplored, int  numberOfTilesToExplore) {
        progressBar.setProgress((float)numberOfTilesExplored/numberOfTilesToExplore);
        executeNextGuiTask.set(true);
    }

    public void setToExplored(AtomicBoolean executeNextGuiTask, List<Vector2D> positions) {
        for (Vector2D pos : positions) {
            tiles[pos.y][pos.x].setToExplored();
        }
        executeNextGuiTask.set(true);
    }

    @Override
    protected Tile getInitialFloorTile() { return new Tile(new TileImage(imageContainer.getFloor()), new TileImage(imageContainer.getUndiscovered())); }

    @Override
    protected ControllerExplorationGUI getController() { return controllerExplorationGUI; }
}
