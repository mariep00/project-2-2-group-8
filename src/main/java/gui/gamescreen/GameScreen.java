package gui.gamescreen;

import controller.Vector2D;
import controller.agent.Agent;
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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class GameScreen extends Application implements TransitionInterface {
    private final ImageContainer imageContainer = ImageContainer.getInstance();
    private Stage stage;
    private ArrayList<Transition> transitions = new ArrayList<>();
    private final ScenarioMap scenarioMap;

    private Tile[][] tiles;
    private ProgressBarCustom progressBar;
    private ArrayList<Vector2D>[] visions;

    public GameScreen(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        loadGameScreen(scenarioMap);
    }

    private void loadGameScreen(ScenarioMap scenarioMap) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

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

        HBox hboxButtons = new HBox(10);
        Button buttonStep = new Button("Step");
        buttonStep.setPrefWidth(130);
        Button buttonPlayTillEnd = new Button("Continue until end");
        buttonPlayTillEnd.setPrefWidth(130);
        hboxButtons.getChildren().addAll(  buttonStep, buttonPlayTillEnd);
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

        ControllerGUI controllerGUI = new ControllerGUI(scenarioMap, this);
        controllerGUI.init();

        buttonStep.setOnAction(e -> {
            controllerGUI.tick();
        });
        buttonPlayTillEnd.setOnAction(e -> {
            controllerGUI.engine();
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

    public void setProgress(int numberOfTilesExplored, int  numberOfTilesToExplore) {
        progressBar.setProgress((float)numberOfTilesExplored/numberOfTilesToExplore);
    }

    public void spawnAgent(Agent agent, Vector2D position) {
        tiles[position.x][position.y].setCharacter(imageContainer.getAgent(AgentType.GUARD, agent.getOrientation()));
    }

    public void moveAgent(Agent agent, Vector2D from, Vector2D to) {
        tiles[from.x][from.y].resetCharacterImage();
        tiles[to.x][to.y].setCharacter(imageContainer.getAgent(AgentType.GUARD, agent.getOrientation()));
    }

    public void setToExplored(Vector2D pos) {
        tiles[pos.x][pos.y].setToExplored();
    }

    public void updateVision(int agentIndex, ArrayList<Vector2D> positions) {
        if (visions[agentIndex] != null) {
            removeVision(visions[agentIndex]);
        }
        visions[agentIndex] = new ArrayList<>(positions);
        for (Vector2D pos : positions) {
            tiles[pos.x][pos.y].setToInVision(imageContainer.getVision());
        }
    }

    private void removeVision(ArrayList<Vector2D> positions) {
        for (Vector2D pos : positions) {
            boolean remove = true;
            for (ArrayList<Vector2D> others : visions) {
                if (!others.equals(positions)) {
                    if (others.contains(pos)) {
                        remove = false;
                        break;
                    }
                }
            }
            if (remove) tiles[pos.x][pos.y].setToOutOfVision();
        }
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
