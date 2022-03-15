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
        for (int i = 0; i < visions.length; i++) {
            visions[i] = new ArrayList<>();
        }
        tiles = new Tile[scenarioMap.getWidth()][scenarioMap.getHeight()];
        controller.maps.Tile[][] tilesController = scenarioMap.getMapGrid();
        for (int x = 0; x < scenarioMap.getWidth(); x++) {
            for (int y = 0; y < scenarioMap.getHeight(); y++) {
                Tile tile;
                if (tilesController[y][x].getType() == controller.maps.Tile.Type.WALL) {
                    // It's a wall. Create a tile with the right wall image
                    tile = new Tile(new TileImage(imageContainer.getWall(getBitSetSurroundingWalls(tilesController, x, y))));
                }
                else if (tilesController[y][x].getType() == controller.maps.Tile.Type.TELEPORT) {
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

        HBox hbox = new HBox();

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
        Button buttonStep = new Button("Step");
        buttonStep.setPrefWidth(130);
        buttonStep.setPrefHeight(30);
        Button buttonRunSimulation = new Button("Run simulation");
        buttonRunSimulation.setPrefWidth(130);
        buttonRunSimulation.setPrefHeight(30);
        Button buttonStopSimulation = new Button("Stop simulation");
        buttonStopSimulation.setPrefWidth(130);
        buttonStopSimulation.setPrefHeight(30);
        hboxButtons.getChildren().addAll(  buttonShowAllVisions, buttonHideAllVisions, buttonStep, buttonRunSimulation, buttonStopSimulation);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);
        Region spacingRegion = new Region();

        hbox.getChildren().addAll(progressBar, spacingRegion, hboxButtons);
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(spacingRegion, Priority.ALWAYS);

        BorderPane.setMargin(hbox, new Insets(5, 5, 5, 5));
        borderPane.setTop(hbox);

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(borderPane.getChildren());

        controllerGUI.init();

        buttonStep.setOnAction(e -> {
            new Thread(controllerGUI::tick).start();
        });
        buttonRunSimulation.setOnAction(e -> controllerGUI.runSimulation());
        buttonStopSimulation.setOnAction(e -> controllerGUI.stopSimulation());
        buttonShowAllVisions.setOnAction(e -> {
            Arrays.fill(showVision, true);
            for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                controllerGUI.showVision(i);
            }
        });
        buttonHideAllVisions.setOnAction(e -> {
            Arrays.fill(showVision, false);
            for (int i = 0; i < scenarioMap.getNumGuards(); i++) {
                controllerGUI.hideVision(i);
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
        if (showVision[agentIndex]) {
            showVision[agentIndex] = false;
            controllerGUI.hideVision(agentIndex);
        }
        else {
            showVision[agentIndex] = true;
            controllerGUI.showVision(agentIndex);
        }
    }

    public void setProgress(AtomicBoolean executeNextGuiTask, int numberOfTilesExplored, int  numberOfTilesToExplore) {
        System.out.println("HERE 1");
        progressBar.setProgress((float)numberOfTilesExplored/numberOfTilesToExplore);
        executeNextGuiTask.set(true);
        System.out.println("HERE 2");
    }

    public void spawnAgent(int agentIndex, Vector2D position) {
        tiles[position.x][position.y].setCharacter(this, imageContainer.getAgent(AgentType.GUARD, controllerGUI.getAgent(agentIndex).getOrientation()), agentIndex);
    }

    public void moveAgent(AtomicBoolean executeNextGuiTask, int agentIndex, Vector2D from, Vector2D to) {
        System.out.println("HERE 3");
        tiles[from.x][from.y].resetCharacterImage();
        tiles[to.x][to.y].setCharacter(this, imageContainer.getAgent(AgentType.GUARD, controllerGUI.getAgent(agentIndex).getOrientation()), agentIndex);
        executeNextGuiTask.set(true);
        System.out.println("HERE 4");
    }

    public void setToExplored(AtomicBoolean executeNextGuiTask, List<Vector2D> positions) {
        System.out.println("HERE 5");
        for (Vector2D pos : positions) {
            tiles[pos.x][pos.y].setToExplored();
        }
        executeNextGuiTask.set(true);
        System.out.println("HERE 6");
    }

    public void updateVision(AtomicBoolean executeNextGuiTask, int agentIndex, ArrayList<Vector2D> positions) {
        System.out.println("HERE 7");
        if (showVision[agentIndex]) {
            if (visions[agentIndex] != null) {
                removeVision(null, agentIndex, visions[agentIndex]);
            }
            visions[agentIndex] = new ArrayList<>(positions);
            showVision(null, positions);
        }
        else visions[agentIndex] = new ArrayList<>(positions);

        System.out.println("HERE 8");
        executeNextGuiTask.set(true);
    }

    public void showVision(AtomicBoolean executeNextGuiTask, ArrayList<Vector2D> positions) {
        System.out.println("HERE 9");
        for (Vector2D pos : positions) {
            tiles[pos.x][pos.y].setToInVision(imageContainer.getVision());
        }
        if (executeNextGuiTask != null) executeNextGuiTask.set(true);
        System.out.println("HERE 10");
    }

    public void removeVision(AtomicBoolean executeNextGuiTask, int agentIndex, ArrayList<Vector2D> positions) {
        System.out.println("HERE 11");
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
        System.out.println("HERE 12");
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
