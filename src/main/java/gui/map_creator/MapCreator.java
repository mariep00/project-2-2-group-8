package gui.map_creator;

import gui.MainGUI;
import gui.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
        gridImage = new Image(this.getClass().getResource("/tiles/grid_square.png").toString());
        ListView<ListItem> listView = getListViewPopulated();

        selectedListItem = listView.getItems().get(0);
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        for (int i = 0; i < 60; i++) {
            for (int j = 0; j < 60; j++) {
                gridPane.add(new Tile(gridImage), i, j);
            }
        }
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Button buttonReset = new Button("Reset");
        Button buttonAllFloor = new Button("Button all floor");
        Button buttonExport = new Button("Export");
        Button buttonPlayGame = new Button("Play game");
        HBox hbox = new HBox(15, buttonReset, buttonAllFloor, buttonExport, buttonPlayGame);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane borderPane = new BorderPane(scrollPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        borderPane.setLeft(listView);
        borderPane.setTop(hbox);
        BorderPane.setMargin(hbox, new Insets(5, 5, 5, 5));

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        listView.setOpacity(0);
        scrollPane.setOpacity(0);
        stage.setScene(scene);
        loadSceneTransition(1.5, listView, scrollPane, hbox);

        listView.getSelectionModel().selectedItemProperty().addListener(e -> {
            selectedListItem = listView.getSelectionModel().getSelectedItem();
        });
        scrollPane.setOnZoom(e -> {
            zooming(gridPane, e.getZoomFactor());
        });
        buttonReset.setOnAction(e -> {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Tile) {
                    ((Tile) node).setImage(gridImage);
                }
            }
        });
        buttonAllFloor.setOnAction(e -> {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Tile) {
                    ((Tile) node).setImage(listView.getItems().get(0).image);
                }
            }
        });
    }

    private ListView<ListItem> getListViewPopulated() {
        ListView<ListItem> listView = new ListView();
        listView.getItems().add(new ListItem("Floor", new Image(this.getClass().getResource("/tiles/floor.jpg").toString())));
        listView.getItems().add(new ListItem("WallBotLeftCor", new Image(this.getClass().getResource("/tiles/botleftcorner.png").toString())));
        listView.getItems().add(new ListItem("WallTopLeftCor", new Image(this.getClass().getResource("/tiles/topleftcorner.png").toString())));
        listView.getItems().add(new ListItem("WallBotRightCor", new Image(this.getClass().getResource("/tiles/botrightcorner.png").toString())));
        listView.getItems().add(new ListItem("WallTopRightCor", new Image(this.getClass().getResource("/tiles/toprightcorner.png").toString())));
        listView.getItems().add(new ListItem("WallLeftRight", new Image(this.getClass().getResource("/tiles/leftright.png").toString())));
        listView.getItems().add(new ListItem("WallTopBot", new Image(this.getClass().getResource("/tiles/top_bot.png").toString())));

        return listView;
    }

    private void zooming(GridPane gridPane, double zoomingFactor) {
        System.out.println(gridPane.getTranslateX() + ", " + gridPane.getTranslateY());
        Scale scaleTransform = new Scale(zoomingFactor, zoomingFactor, MouseInfo.getPointerInfo().getLocation().x-gridPane.getBoundsInLocal().getMinX(), MouseInfo.getPointerInfo().getLocation().y-gridPane.getBoundsInLocal().getMinY());
        gridPane.getTransforms().add(scaleTransform);
    }
    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }
}

class ListItem {
    Image image;
    String name;

    public ListItem(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public String toString() { return name; }
}
