package gui.gamescreen;

import controller.Agent;
import controller.EndingExplorationMap;
import controller.ScenarioMap;
import controller.Vector2D;
import gui.MainGUI;
import gui.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameScreen extends Application implements TransitionInterface {
    private final ImageContainer imageContainer = ImageContainer.getInstance();
    private Stage stage;
    private ArrayList<Transition> transitions = new ArrayList<>();
    private final ScenarioMap scenarioMap;

    private Tile[][] tiles;
    private ProgressBar progressBar;

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

        tiles = new Tile[scenarioMap.getWidth()][scenarioMap.getHeight()];
        controller.Tile[][] tilesController = scenarioMap.getMapGrid();
        for (int x = 0; x < scenarioMap.getWidth(); x++) {
            for (int y = 0; y < scenarioMap.getHeight(); y++) {
                Tile tile;
                if (tilesController[y][x].getType() == controller.Tile.Type.WALL) {
                    // It's a wall. Create a tile with the right wall image
                    tile = new Tile(new TileImage(imageContainer.getWall(getBitSetSurroundingWalls(tilesController, x, y))));
                }
                else if (tilesController[y][x].getType() == controller.Tile.Type.TELEPORT) {
                    // For now a floor, change to teleport image later
                    tile = new Tile(new TileImage(imageContainer.getTeleport()));
                }
                // If it's none of above, it's always a floor
                else {
                    tile = new Tile(new TileImage(imageContainer.getFloor()));
                }

                if (tilesController[y][x].isShaded()) {
                    tile.setShaded(imageContainer.getShaded());
                }
                if (tilesController[y][x].getType() == controller.Tile.Type.TARGET_AREA) {
                    tile.setTargetArea(imageContainer.getTargetArea());
                }

                gridPane.add(tile, x, y);
                tiles[x][y] = tile;
            }
        }

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(gridPane);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");

        BorderPane borderPane = new BorderPane(scrollPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);

        HBox hbox = new HBox();

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);

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
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        HBox.setHgrow(spacingRegion, Priority.ALWAYS);

        BorderPane.setMargin(hbox, new Insets(5, 5, 5, 5));
        borderPane.setTop(hbox);

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(borderPane.getChildren());

        sampleGame();
    }

    private void sampleGame() {
        Vector2D[] positions = new Vector2D[4];
        positions[0] = new Vector2D(10, 10);
        positions[1] = new Vector2D(15, 10);
        positions[2] = new Vector2D(12, 8);
        positions[3] = new Vector2D(16, 13);

        Agent[] agents = {
                new AgentGUI(10, 10, 0, 1, new EndingExplorationMap(scenarioMap), AgentType.GUARD),
                new AgentGUI(10, 10, 0, 1, new EndingExplorationMap(scenarioMap), AgentType.GUARD),
                new AgentGUI(10, 10, 0, 1, new EndingExplorationMap(scenarioMap), AgentType.GUARD),
                new AgentGUI(10, 10, 0, 1, new EndingExplorationMap(scenarioMap), AgentType.GUARD)
        };

        for (int i = 0; i < agents.length; i++) {
            spawnAgent((AgentGUI) agents[i], positions[i]);
        }

        final int[] index = {0};
        Runnable helloRunnable = () -> {
            setProgress(index[0] += 100, 5000);
            for (int i = 0; i < positions.length; i++) {
                moveAgent((AgentGUI) agents[i], positions[i], positions[i].getSide(Vector2D.Direction.EAST));
                positions[i] = positions[i].getSide(Vector2D.Direction.EAST);
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 3, TimeUnit.SECONDS);
    }

    private BitSet getBitSetSurroundingWalls(controller.Tile[][] tiles, int x, int y) {
        BitSet bitSet = new BitSet(8);
        byte count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    if (y + i >= 0 && x + j >= 0 && y + i < tiles.length && x + j < tiles[y].length) {
                        if (tiles[y + i][x + j].getType() == controller.Tile.Type.WALL) {
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

    public void spawnAgent(AgentGUI agentGUI, Vector2D position) {
        tiles[position.x][position.y].setCharacter(imageContainer.getAgent(agentGUI.agentType, agentGUI.getOrientation()));
    }

    public void moveAgent(AgentGUI agentGUI, Vector2D from, Vector2D to) {
        tiles[from.x][from.y].resetCharacterImage();
        tiles[to.x][to.y].setCharacter(imageContainer.getAgent(agentGUI.agentType, agentGUI.getOrientation()));
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
