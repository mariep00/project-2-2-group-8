package gui.gamescreen;

import datastructures.Vector2D;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.maps.ScenarioMap;
import gui.MainGUI;
import gui.TransitionInterface;
import gui.gamescreen.controller.ControllerExplorationGUI;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameScreen extends Application implements TransitionInterface {
    private final ControllerExplorationGUI controllerExplorationGUI;
    private final ImageContainer imageContainer = ImageContainer.getInstance();
    private Stage stage;
    private ArrayList<Transition> transitions = new ArrayList<>();
    private final ScenarioMap scenarioMap;

    private Tile[][] tiles;
    private ProgressBarCustom progressBar;
    private boolean[] showVision;

    public GameScreen(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
        this.controllerExplorationGUI = new ControllerExplorationGUI(scenarioMap, new EndingExploration(scenarioMap), this);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        loadGameScreen(scenarioMap);
    }

    private void loadGameScreen(ScenarioMap scenarioMap) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        showVision = new boolean[scenarioMap.getNumGuards()];
        tiles = new Tile[scenarioMap.getHeight()][scenarioMap.getWidth()];
        gamelogic.maps.Tile[][] tilesController = scenarioMap.getMapGrid();

        for (int x = 0; x < scenarioMap.getWidth(); x++) {
            for (int y = 0; y < scenarioMap.getHeight(); y++) {
                Tile tile;
                if (tilesController[y][x].getType() == gamelogic.maps.Tile.Type.WALL) {
                    // It's a wall. Create a tile with the right wall image
                    tile = new Tile(new TileImage(imageContainer.getWall(getBitSetSurroundingWalls(tilesController, x, y))));
                }
                else if (tilesController[y][x].getType() == gamelogic.maps.Tile.Type.TELEPORT_ENTRANCE) {
                    // For now a floor, change to teleport image later
                    tile = new Tile(new TileImage(imageContainer.getTeleport()));
                }
                // If it's none of above, it's always a floor
                else {
                    tile = new Tile(new TileImage(imageContainer.getFloor()), new TileImage(imageContainer.getUndiscovered()));
                }

                if (tilesController[y][x].isShaded()) {
                    tile.setShaded(imageContainer.getShaded());
                }
                if (tilesController[y][x].getType() == gamelogic.maps.Tile.Type.TARGET_AREA) {
                    tile.setTargetArea(imageContainer.getTargetArea());
                }

                gridPane.add(tile, x, y);
                tiles[y][x] = tile;
                tile.addEventFilter(MouseEvent.MOUSE_CLICKED,e -> {
                    tile.getTileImageAgent().onClick();
                    e.consume();
                });
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");

        BorderPane borderPane = new BorderPane(scrollPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);

        progressBar = new ProgressBarCustom();
        progressBar.getProgressBar().setPrefWidth(400);
        progressBar.getProgressBar().setPrefHeight(35);

        HBox hboxButtons = new HBox(10);
        Button buttonShowAllVisions = new Button();
        ImageView showVisionImageView = new ImageView(imageContainer.getShowVision());
        showVisionImageView.setFitWidth(25);
        showVisionImageView.setFitHeight(25);
        buttonShowAllVisions.setGraphic(showVisionImageView);
        buttonShowAllVisions.setPrefWidth(35);
        buttonShowAllVisions.setPrefHeight(35);

        Button buttonHideAllVisions = new Button();
        ImageView hideVisionImageView = new ImageView(imageContainer.getHideVision());
        hideVisionImageView.setFitWidth(25);
        hideVisionImageView.setFitHeight(25);
        buttonHideAllVisions.setGraphic(hideVisionImageView);
        buttonHideAllVisions.setPrefWidth(35);
        buttonHideAllVisions.setPrefHeight(35);

        Button buttonStep = new Button();
        ImageView stepImageView = new ImageView(imageContainer.getStep());
        stepImageView.setFitWidth(25);
        stepImageView.setFitHeight(25);
        buttonStep.setGraphic(stepImageView);
        buttonStep.setPrefWidth(35);
        buttonStep.setPrefHeight(35);

        Button buttonPlaySimulation = new Button();
        ImageView playImageView = new ImageView(imageContainer.getPlay());
        playImageView.setFitWidth(25);
        playImageView.setFitHeight(25);
        buttonPlaySimulation.setGraphic(playImageView);
        buttonPlaySimulation.setPrefWidth(35);
        buttonPlaySimulation.setPrefHeight(35);

        Button buttonStopSimulation = new Button();
        ImageView stopImageView = new ImageView(imageContainer.getStop());
        stopImageView.setFitWidth(25);
        stopImageView.setFitHeight(25);
        buttonStopSimulation.setGraphic(stopImageView);
        buttonStopSimulation.setPrefWidth(35);
        buttonStopSimulation.setPrefHeight(35);

        Slider simulationSlider = new Slider();
        simulationSlider.setMax(800);
        simulationSlider.setMin(5);
        simulationSlider.setMajorTickUnit(266.67);
        simulationSlider.setShowTickMarks(true);
        simulationSlider.setValue(400);
        simulationSlider.setPrefWidth(260);
        simulationSlider.setPrefHeight(35);

        hboxButtons.getChildren().addAll(  buttonShowAllVisions, buttonHideAllVisions, simulationSlider, buttonStep, buttonPlaySimulation, buttonStopSimulation);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(progressBar);
        AnchorPane.setLeftAnchor(progressBar, 0.0);
        anchorPane.getChildren().add(hboxButtons);
        AnchorPane.setRightAnchor(hboxButtons, 0.0);

        BorderPane.setMargin(anchorPane, new Insets(5, 5, 5, 5));
        borderPane.setTop(anchorPane);

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(borderPane.getChildren());

        gridPane.setHgap(-1);
        gridPane.setVgap(-1);

        simulationSlider.setOnMouseDragged(e -> {
            controllerExplorationGUI.setSimulationDelay((int)(simulationSlider.maxProperty().get()-simulationSlider.getValue()));
        });
        simulationSlider.setOnMouseReleased(e -> {
            controllerExplorationGUI.setSimulationDelay((int)(simulationSlider.maxProperty().get()-simulationSlider.getValue()));
        });

        buttonStep.setOnAction(e -> {
            if (controllerExplorationGUI.getRunSimulation().get()) {
                buttonPlaySimulation.setId("");
                controllerExplorationGUI.stopSimulation();
            }
            new Thread(controllerExplorationGUI::tick).start();
        });
        buttonPlaySimulation.setOnAction(e -> {
            buttonPlaySimulation.setId("play_button_clicked");
            if (!controllerExplorationGUI.getRunSimulation().get()) controllerExplorationGUI.runSimulation();
            else {
                controllerExplorationGUI.stopSimulation();
                buttonPlaySimulation.setId("");
            }
        });
        buttonStopSimulation.setOnAction(e -> {
            buttonPlaySimulation.setId("");
            controllerExplorationGUI.stopSimulation();
        });
        buttonShowAllVisions.setOnAction(e -> {
            if (!controllerExplorationGUI.getRunSimulation().get()) {
                Arrays.fill(showVision, true);
                for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                    controllerExplorationGUI.showVision(i);
                }
            }
        });
        buttonHideAllVisions.setOnAction(e -> {
            if (!controllerExplorationGUI.getRunSimulation().get()) {
                Arrays.fill(showVision, false);
                for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                    controllerExplorationGUI.hideVision(i);
                }
            }
        });

        controllerExplorationGUI.init();
    }

    private BitSet getBitSetSurroundingWalls(gamelogic.maps.Tile[][] tiles, int x, int y) {
        BitSet bitSet = new BitSet(8);
        byte count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    if (y + i >= 0 && x + j >= 0 && y + i < tiles.length && x + j < tiles[y].length) {
                        if (tiles[y + i][x + j].getType() == gamelogic.maps.Tile.Type.WALL) {
                            bitSet.set(count);
                        }
                    }
                    count++;
                }
            }
        }
        return bitSet;
    }

    public void pauseGame() {
        controllerExplorationGUI.pauseThreads();
    }
    public void continueGame() {
        controllerExplorationGUI.continueThreads();
    }

    public void toggleVision(int agentIndex) {
        if (!controllerExplorationGUI.getRunSimulation().get()) {
            if (showVision[agentIndex]) {
                showVision[agentIndex] = false;
                controllerExplorationGUI.hideVision(agentIndex);
            } else {
                showVision[agentIndex] = true;
                controllerExplorationGUI.showVision(agentIndex);
            }
        }
    }

    public void setProgress(AtomicBoolean executeNextGuiTask, int numberOfTilesExplored, int  numberOfTilesToExplore) {
        progressBar.setProgress((float)numberOfTilesExplored/numberOfTilesToExplore);
        executeNextGuiTask.set(true);
    }

    public void spawnAgent(int agentIndex, Vector2D position) {
        tiles[position.y][position.x].setCharacter(this, imageContainer.getAgent(AgentType.GUARD, controllerExplorationGUI.getAgent(agentIndex).getOrientation()), agentIndex);
    }

    public void moveAgent(AtomicBoolean executeNextGuiTask, int agentIndex, Vector2D from, Vector2D to) {
        tiles[from.y][from.x].resetCharacterImage();
        tiles[to.y][to.x].setCharacter(this, imageContainer.getAgent(AgentType.GUARD, controllerExplorationGUI.getAgent(agentIndex).getOrientation()), agentIndex);
        executeNextGuiTask.set(true);
    }

    public void setToExplored(AtomicBoolean executeNextGuiTask, List<Vector2D> positions) {
        for (Vector2D pos : positions) {
            tiles[pos.y][pos.x].setToExplored();
        }
        executeNextGuiTask.set(true);
    }

    public void updateVision(AtomicBoolean executeNextGuiTask, int agentIndex, List<Vector2D> oldVision, List<Vector2D> newVision) {
        if (showVision[agentIndex]) {
            removeVision(null, agentIndex, oldVision);
            showVision(null, newVision);
        }
        executeNextGuiTask.set(true);
    }

    public void showVision(AtomicBoolean executeNextGuiTask, List<Vector2D> visionToShow) {
        for (Vector2D pos : visionToShow) {
            tiles[pos.y][pos.x].setToInVision(imageContainer.getVision());
        }
        if (executeNextGuiTask != null) executeNextGuiTask.set(true);
    }

    public void removeVision(AtomicBoolean executeNextGuiTask, int agentIndex, List<Vector2D> visionToRemove) {
        for (Vector2D pos : visionToRemove) {
            tiles[pos.y][pos.x].setToOutOfVision();
        }
        if (executeNextGuiTask != null) executeNextGuiTask.set(true);
    }

    public boolean getShowVision(int agentIndex) { return showVision[agentIndex]; }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
