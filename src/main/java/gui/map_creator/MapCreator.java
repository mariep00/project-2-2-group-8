package gui.map_creator;

import gui.MainGUI;
import gui.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MapCreator extends Application implements TransitionInterface {
    private Stage stage;
    private final ArrayList<Transition> transitions = new ArrayList<>();
    public static Image gridImage;
    public static ListItem selectedListItem;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        //TODO Change this to the size the user wants --> make input for it
        final int nrOfTilesWidth = 80;
        final int nrOfTilesHeight = 80;

        gridImage = new Image(this.getClass().getResource("/tiles/base/grid_square.png").toString());
        ListView<ListItem> listView = getListViewPopulated();
        listView.setPrefWidth(150);

        selectedListItem = listView.getItems().get(0);
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        Tile[][] tiles = new Tile[nrOfTilesWidth][nrOfTilesHeight];
        for (int i = 0; i < nrOfTilesWidth; i++) {
            for (int j = 0; j < nrOfTilesHeight; j++) {
                Tile tile = new Tile(new TileImage(gridImage));
                gridPane.add(tile, i, j);
                tiles[i][j] = tile;
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);

        Button buttonReset = new Button("Reset map");
        Button buttonAllFloor = new Button("Set all to floor");
        Button buttonExport = new Button("Export map");
        Button buttonPlayGame = new Button("Play game with map");
        HBox hbox = new HBox(15, buttonReset, buttonAllFloor, buttonExport, buttonPlayGame);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane borderPane = new BorderPane(scrollPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        borderPane.setLeft(listView);
        borderPane.setTop(hbox);
        BorderPane.setMargin(hbox, new Insets(5, 5, 5, 5));

        scrollPane.setStyle("-fx-focus-color: transparent;");
        listView.setStyle("-fx-focus-color: transparent;");

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        listView.setOpacity(0);
        scrollPane.setOpacity(0);
        hbox.setOpacity(0);
        stage.setScene(scene);
        loadSceneTransition( listView, scrollPane, hbox);

        listView.getSelectionModel().selectedItemProperty().addListener(e -> {
            selectedListItem = listView.getSelectionModel().getSelectedItem();
        });
        scrollPane.setOnZoom(e -> {
            //zooming(gridPane, e.getZoomFactor());
        });
        buttonReset.setOnAction(e -> {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Tile) {
                    ((Tile) node).resetTile();
                }
            }
        });
        buttonAllFloor.setOnAction(e -> {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Tile) {
                    ((Tile) node).setBaseImage(listView.getItems().get(0).image);
                }
            }
        });
        gridPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            Bounds boundsGridPaneInScene = gridPane.localToScene(gridPane.getBoundsInLocal());
            double posX = e.getSceneX() - boundsGridPaneInScene.getMinX();
            double posY = e.getSceneY() - boundsGridPaneInScene.getMinY();

            int indexX = (int) Math.floor((posX / Tile.tileSize));
            int indexY = (int) Math.floor((posY / Tile.tileSize));

            if (indexX < tiles.length && indexY < tiles[0].length && indexX >= 0 && indexY >= 0) {
                if (e.isPrimaryButtonDown()) {
                    tiles[indexX][indexY].setImageToSelected();
                }
                else if (e.isSecondaryButtonDown()) {
                    tiles[indexX][indexY].resetTile();
                }
            }
        });
    }

    private ListView<ListItem> getListViewPopulated() {
        ListView<ListItem> listView = new ListView();
        listView.getItems().add(new ListItem("Floor", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/floor.jpg").toString())));
        listView.getItems().add(new ListItem("Wall center", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_center.jpg").toString())));
        listView.getItems().add(new ListItem("Wall front", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_front.jpg").toString())));
        listView.getItems().add(new ListItem("Wall left", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_left.jpg").toString())));
        listView.getItems().add(new ListItem("Wall right", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_right.jpg").toString())));
        listView.getItems().add(new ListItem("Wall sides", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_sides.jpg").toString())));
        listView.getItems().add(new ListItem("Wall top", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_top.jpg").toString())));
        listView.getItems().add(new ListItem("Wall top corner left", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_top_corner_left.jpg").toString())));
        listView.getItems().add(new ListItem("Wall top corner right", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_top_corner_right.jpg").toString())));
        listView.getItems().add(new ListItem("Wall top cornered", ImageType.BASE,new Image(this.getClass().getResource("/tiles/base/wall_top_cornered.jpg").toString())));
        listView.getItems().add(new ListItem("Guard", ImageType.CHARACTER,new Image(this.getClass().getResource("/tiles/character/guard/guard.png").toString())));
        return listView;
    }

    private void zooming(GridPane gridPane, double zoomingFactor) {
        Scale scaleTransform = new Scale(zoomingFactor, zoomingFactor, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
        gridPane.getTransforms().add(scaleTransform);
    }

    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }
}

class ListItem {
    String name;
    ImageType imageType;
    Image image;

    public ListItem(String name, ImageType imageType, Image image) {
        this.name = name;
        this.imageType = imageType;
        this.image = image;
    }

    public String toString() { return name; }
}

enum ImageType {
    BASE,
    CHARACTER,
    OTHER
}

