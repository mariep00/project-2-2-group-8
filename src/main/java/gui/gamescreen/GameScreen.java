package gui.gamescreen;

import controller.Vector2D;
import controller.maps.ScenarioMap;
import gui.MainGUI;
import gui.TransitionInterface;
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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameScreen extends Application implements TransitionInterface {
    private final ControllerGUI controllerGUI;
    private final ImageContainer imageContainer = ImageContainer.getInstance();
    private Stage stage;
    private ArrayList<Transition> transitions = new ArrayList<>();
    private final ScenarioMap scenarioMap;

    private Tile[][] tiles;
    private ProgressBarCustom progressBar;
    private ArrayList<Vector2D>[] visions;
    private boolean[] showVision;

    public GameScreen(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
        this.controllerGUI = new ControllerGUI(scenarioMap, this);
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
        visions = (ArrayList<Vector2D>[]) new ArrayList[scenarioMap.getNumGuards()];
        tiles = new Tile[scenarioMap.getWidth()][scenarioMap.getHeight()];
        controller.maps.Tile[][] tilesController = scenarioMap.getMapGrid();
        for (int x = 0; x < scenarioMap.getWidth(); x++) {
            for (int y = 0; y < scenarioMap.getHeight(); y++) {
                Tile tile;
                if (tilesController[y][x].getType() == controller.maps.Tile.Type.WALL) {
                    // It's a wall. Create a tile with the right wall image
                    tile = new Tile(new TileImage(imageContainer.getWall(getBitSetSurroundingWalls(tilesController, x, y))));
                }
                else if (tilesController[y][x].getType() == controller.maps.Tile.Type.TELEPORT_ENTRANCE) {
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
                if (tilesController[y][x].getType() == controller.maps.Tile.Type.TARGET_AREA) {
                    tile.setTargetArea(imageContainer.getTargetArea());
                }

                gridPane.add(tile, x, y);
                tiles[x][y] = tile;
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
        progressBar.getProgressBar().setPrefHeight(30);

        HBox hboxButtons = new HBox(10);
        Button buttonShowAllVisions = new Button("Show vision");
        buttonShowAllVisions.setPrefWidth(130);
        buttonShowAllVisions.setPrefHeight(30);
        Button buttonHideAllVisions = new Button("Hide vision");
        buttonHideAllVisions.setPrefWidth(130);
        buttonHideAllVisions.setPrefHeight(30);

        Button buttonStep = new Button();
        ImageView stepImageView = new ImageView(imageContainer.getStep());
        stepImageView.setFitWidth(20);
        stepImageView.setFitHeight(20);
        buttonStep.setGraphic(stepImageView);
        buttonStep.setPrefWidth(30);
        buttonStep.setPrefHeight(30);

        Button buttonPlaySimulation = new Button();
        ImageView playImageView = new ImageView(imageContainer.getPlay());
        playImageView.setFitWidth(20);
        playImageView.setFitHeight(20);
        buttonPlaySimulation.setGraphic(playImageView);
        buttonPlaySimulation.setPrefWidth(30);
        buttonPlaySimulation.setPrefHeight(30);

        Button buttonStopSimulation = new Button();
        ImageView stopImageView = new ImageView(imageContainer.getStop());
        stopImageView.setFitWidth(20);
        stopImageView.setFitHeight(20);
        buttonStopSimulation.setGraphic(stopImageView);
        buttonStopSimulation.setPrefWidth(30);
        buttonStopSimulation.setPrefHeight(30);

        Slider simulationSlider = new Slider();
        simulationSlider.setMax(800);
        simulationSlider.setMin(5);
        simulationSlider.setMajorTickUnit(266.67);
        simulationSlider.setShowTickMarks(true);
        simulationSlider.setValue(400);
        simulationSlider.setPrefWidth(260);
        simulationSlider.setPrefHeight(30);

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

        controllerGUI.init();
        gridPane.setHgap(-1);
        gridPane.setVgap(-1);

        simulationSlider.setOnMouseDragged(e -> {
            controllerGUI.setSimulationDelay((int)simulationSlider.getValue());
        });
        simulationSlider.setOnMouseReleased(e -> {
            controllerGUI.setSimulationDelay((int)simulationSlider.getValue());
        });

        buttonStep.setOnAction(e -> {
            if (controllerGUI.getRunSimulation().get()) {
                buttonPlaySimulation.setId("");
                controllerGUI.stopSimulation();
            }
            new Thread(controllerGUI::tick).start();
        });
        buttonPlaySimulation.setOnAction(e -> {
            buttonPlaySimulation.setId("play_button_clicked");
            controllerGUI.runSimulation();
        });
        buttonStopSimulation.setOnAction(e -> {
            buttonPlaySimulation.setId("");
            controllerGUI.stopSimulation();
        });
        buttonShowAllVisions.setOnAction(e -> {
            if (!controllerGUI.getRunSimulation().get()) {
                Arrays.fill(showVision, true);
                for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                    controllerGUI.showVision(i);
                }
            }
        });
        buttonHideAllVisions.setOnAction(e -> {
            if (!controllerGUI.getRunSimulation().get()) {
                Arrays.fill(showVision, false);
                for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                    controllerGUI.hideVision(i);
                }
            }
        });
    }

    private BitSet getBitSetSurroundingWalls(controller.maps.Tile[][] tiles, int x, int y) {
        BitSet bitSet = new BitSet(8);
        byte count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    if (y + i >= 0 && x + j >= 0 && y + i < tiles.length && x + j < tiles[y].length) {
                        if (tiles[y + i][x + j].getType() == controller.maps.Tile.Type.WALL) {
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
        controllerGUI.pauseThreads();
    }
    public void continueGame() {
        controllerGUI.continueThreads();
    }

    public void toggleVision(int agentIndex) {
        if (!controllerGUI.getRunSimulation().get()) {
            if (showVision[agentIndex]) {
                showVision[agentIndex] = false;
                controllerGUI.hideVision(agentIndex);
            } else {
                showVision[agentIndex] = true;
                controllerGUI.showVision(agentIndex);
            }
        }
    }

    public void setProgress(AtomicBoolean executeNextGuiTask, int numberOfTilesExplored, int  numberOfTilesToExplore) {
        progressBar.setProgress((float)numberOfTilesExplored/numberOfTilesToExplore);
        executeNextGuiTask.set(true);
    }

    public void spawnAgent(int agentIndex, Vector2D position) {
        tiles[position.x][position.y].setCharacter(this, imageContainer.getAgent(AgentType.GUARD, controllerGUI.getAgent(agentIndex).getOrientation()), agentIndex);
    }

    public void moveAgent(AtomicBoolean executeNextGuiTask, int agentIndex, Vector2D from, Vector2D to) {
        tiles[from.x][from.y].resetCharacterImage();
        tiles[to.x][to.y].setCharacter(this, imageContainer.getAgent(AgentType.GUARD, controllerGUI.getAgent(agentIndex).getOrientation()), agentIndex);
        executeNextGuiTask.set(true);
    }

    public void setToExplored(AtomicBoolean executeNextGuiTask, List<Vector2D> positions) {
        for (Vector2D pos : positions) {
            tiles[pos.x][pos.y].setToExplored();
        }
        executeNextGuiTask.set(true);
    }

    public void updateVision(AtomicBoolean executeNextGuiTask, int agentIndex, List<Vector2D> positions) {
        if (showVision[agentIndex]) {
            if (visions[agentIndex] != null) {
                removeVision(null, agentIndex, visions[agentIndex]);
            }
            visions[agentIndex] = new ArrayList<>(positions);
            showVision(null, positions);
        }
        else visions[agentIndex] = new ArrayList<>(positions);

        executeNextGuiTask.set(true);
    }

    public void showVision(AtomicBoolean executeNextGuiTask, List<Vector2D> positions) {
        for (Vector2D pos : positions) {
            tiles[pos.x][pos.y].setToInVision(imageContainer.getVision());
        }
        if (executeNextGuiTask != null) executeNextGuiTask.set(true);
    }

    public void removeVision(AtomicBoolean executeNextGuiTask, int agentIndex, List<Vector2D> positions) {
        for (Vector2D pos : positions) {
            boolean remove = true;
            outer:
            for (int i = 0; i < visions.length; i++) {
                if (i != agentIndex && showVision[i]) {
                    ArrayList<Vector2D> others = visions[i];
                    for (Vector2D posOther : others) {
                        if (posOther.equals(pos)) {
                            remove = false;
                            break outer;
                        }
                    }
                }
            }
            if (remove) tiles[pos.x][pos.y].setToOutOfVision();
        }
        if (executeNextGuiTask != null) executeNextGuiTask.set(true);
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
