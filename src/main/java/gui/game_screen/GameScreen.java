package gui.game_screen;

import Controller.ScenarioMap;
import gui.TransitionInterface;
import gui.map_creator.TileMapCreator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends Application implements TransitionInterface {
    private Stage stage;
    private ArrayList<Transition> transitions = new ArrayList<>();
    private final ScenarioMap scenarioMap;
    private Image[] wallImages;
    private Image floor;

    public GameScreen(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        initializeImageArrays();
        loadGameScreen(scenarioMap);
    }

    private void initializeImageArrays() {
        wallImages = new Image[9];
        wallImages[0] = new Image(this.getClass().getResource("/tiles/base/wall_center.jpg").toString());
        wallImages[1] = new Image(this.getClass().getResource("/tiles/base/wall_front.jpg").toString());
        wallImages[2] = new Image(this.getClass().getResource("/tiles/base/wall_left.jpg").toString());
        wallImages[3] = new Image(this.getClass().getResource("/tiles/base/wall_right.jpg").toString());
        wallImages[4] = new Image(this.getClass().getResource("/tiles/base/wall_sides.jpg").toString());
        wallImages[5] = new Image(this.getClass().getResource("/tiles/base/wall_top.jpg").toString());
        wallImages[6] = new Image(this.getClass().getResource("/tiles/base/wall_top_corner_left.jpg").toString());
        wallImages[7] = new Image(this.getClass().getResource("/tiles/base/wall_top_corner_right.jpg").toString());
        wallImages[8] = new Image(this.getClass().getResource("/tiles/base/wall_top_cornered.jpg").toString());

        floor = new Image(this.getClass().getResource("/tiles/base/floor.jpg").toString());
    }

    private void loadGameScreen(ScenarioMap scenarioMap) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        Tile[][] tiles = new Tile[scenarioMap.getWidth()][scenarioMap.getHeight()];
        Controller.Tile[][] tilesController = scenarioMap.getMapGrid();
        for (int i = 0; i < scenarioMap.getWidth(); i++) {
            for (int j = 0; j < scenarioMap.getHeight(); j++) {
                TileMapCreator tile = new TileMapCreator(getGuiTile(tilesController[i][j]));
                gridPane.add(tile, i, j);
                tiles[i][j] = tile;
            }
        }

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(gridPane);
        //scrollPane.setPannable(true); // Causes issues when "painting" while dragging
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
    }

    private TileImage getGuiTile(Controller.Tile controllerTile) {
        switch (controllerTile.getType()) {
            case WALL: {
                return new TileImage(wallImages[0]);
            }
            case FLOOR: {
                return new TileImage(floor);
            }
        }
        return null;
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
