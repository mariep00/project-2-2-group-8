package gui.gamescreen;

import datastructures.Vector2D;
import gamelogic.controller.Controller;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.controller.ControllerGUIInterface;
import gui.util.HelperGUI;
import gui.util.ImageContainer;
import gui.util.MainGUI;
import gui.util.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameScreen extends Application implements TransitionInterface {
    protected final ImageContainer imageContainer = ImageContainer.getInstance();
    private Stage stage;
    private final ArrayList<Transition> transitions = new ArrayList<>();
    private final ScenarioMap scenarioMap;

    protected Tile[][] tiles;
    private boolean[] showVision;

    public GameScreen(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Scene scene = loadGameScreen(scenarioMap);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(scene.getRoot().getChildrenUnmodifiable());
    }

    private Scene loadGameScreen(ScenarioMap scenarioMap) {
        showVision = new boolean[scenarioMap.getNumGuards()];

        GridPane gridPane = loadGridPane();
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");

        BorderPane borderPane = new BorderPane(scrollPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);

        AnchorPane anchorPane = loadInformationBar();

        BorderPane.setMargin(anchorPane, new Insets(5, 5, 5, 5));
        borderPane.setTop(anchorPane);

        return new Scene(borderPane);
    }

    protected AnchorPane loadInformationBar() {
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

        SimulationSpeedSlider simulationSlider = new SimulationSpeedSlider();

        hboxButtons.getChildren().addAll(  buttonShowAllVisions, buttonHideAllVisions, simulationSlider, buttonStep, buttonPlaySimulation, buttonStopSimulation);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(hboxButtons);
        AnchorPane.setRightAnchor(hboxButtons, 0.0);

        simulationSlider.setOnMouseDragged(e -> {
            getController().setSimulationDelay((int)(simulationSlider.slider.maxProperty().get()-simulationSlider.slider.getValue()));
        });
        simulationSlider.setOnMouseReleased(e -> {
            getController().setSimulationDelay((int)(simulationSlider.slider.maxProperty().get()-simulationSlider.slider.getValue()));
        });

        buttonStep.setOnAction(e -> {
            if (getController().getRunSimulation().get()) {
                buttonPlaySimulation.setId("");
                getController().stopSimulation();
            }
            new Thread(((Controller) getController())::tick).start();
        });
        buttonPlaySimulation.setOnAction(e -> {
            buttonPlaySimulation.setId("play_button_clicked");
            if (!getController().getRunSimulation().get()) getController().runSimulation();
            else {
                getController().stopSimulation();
                buttonPlaySimulation.setId("");
            }
        });
        buttonStopSimulation.setOnAction(e -> {
            buttonPlaySimulation.setId("");
            getController().stopSimulation();
        });
        buttonShowAllVisions.setOnAction(e -> {
            if (!getController().getRunSimulation().get()) {
                Arrays.fill(showVision, true);
                for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                    getController().showVision(i);
                }
            }
        });
        buttonHideAllVisions.setOnAction(e -> {
            if (!getController().getRunSimulation().get()) {
                Arrays.fill(showVision, false);
                for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                    getController().hideVision(i);
                }
            }
        });

        return anchorPane;
    }

    private GridPane loadGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(-1);
        gridPane.setVgap(-1);

        tiles = new Tile[scenarioMap.getHeight()][scenarioMap.getWidth()];
        gamelogic.maps.Tile[][] tilesScenarioMap = scenarioMap.getMapGrid();

        for (int x = 0; x < scenarioMap.getWidth(); x++) {
            for (int y = 0; y < scenarioMap.getHeight(); y++) {
                Tile tile;
                if (tilesScenarioMap[y][x].getType() == gamelogic.maps.Tile.Type.WALL) {
                    // It's a wall. Create a tile with the right wall image
                    tile = new Tile(new TileImage(imageContainer.getWall(HelperGUI.getBitSetSurroundingWalls(tilesScenarioMap, x, y))));
                }
                else if (tilesScenarioMap[y][x].getType() == gamelogic.maps.Tile.Type.TELEPORT_ENTRANCE) {
                    tile = new Tile(new TileImage(imageContainer.getTeleport()));
                }
                // If it's none of above, it's always a floor
                else {
                    tile = getInitialFloorTile();
                }

                if (tilesScenarioMap[y][x].isShaded()) {
                    tile.setShaded(imageContainer.getShaded());
                }
                if (tilesScenarioMap[y][x].getType() == gamelogic.maps.Tile.Type.TARGET_AREA) {
                    tile.setTargetArea(imageContainer.getTargetArea());
                }

                gridPane.add(tile, x, y);
                tiles[y][x] = tile;
                tile.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                    tile.getTileImageAgent().onClick();
                    e.consume();
                });
            }
        }

        return gridPane;
    }

    protected Tile getInitialFloorTile() { return null; }

    public void pauseGame() {
        getController().pauseThreads();
    }
    public void continueGame() {
        getController().continueThreads();
    }

    public void toggleVision(int agentIndex) {
        if (!getController().getRunSimulation().get()) {
            if (showVision[agentIndex]) {
                showVision[agentIndex] = false;
                getController().hideVision(agentIndex);
            } else {
                showVision[agentIndex] = true;
                getController().showVision(agentIndex);
            }
        }
    }

    public void spawnAgent(int agentIndex, Vector2D position, AgentType agentType) {
        tiles[position.y][position.x].setCharacter(this, imageContainer.getAgent(agentType, ((Controller) getController()).getAgent(agentIndex).getOrientation()), agentIndex);
    }

    public void moveAgent(AtomicBoolean executeNextGuiTask, int agentIndex, Vector2D from, Vector2D to, AgentType agentType) {
        tiles[from.y][from.x].resetCharacterImage();
        tiles[to.y][to.x].setCharacter(this, imageContainer.getAgent(agentType, ((Controller) getController()).getAgent(agentIndex).getOrientation()), agentIndex);
        executeNextGuiTask.set(true);
    }

    public void updateVision(AtomicBoolean executeNextGuiTask, int agentIndex, List<Vector2D> oldVision, List<Vector2D> newVision) {
        if (showVision[agentIndex]) {
            removeVision(null, oldVision);
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

    public void removeVision(AtomicBoolean executeNextGuiTask, List<Vector2D> visionToRemove) {
        for (Vector2D pos : visionToRemove) {
            tiles[pos.y][pos.x].setToOutOfVision();
        }
        if (executeNextGuiTask != null) executeNextGuiTask.set(true);
    }

    public boolean getShowVision(int agentIndex) { return showVision[agentIndex]; }

    protected ControllerGUIInterface getController() { return null; }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
