package gui.map_creator;

import gui.MainGUI;
import gui.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MapCreator extends Application implements TransitionInterface {
    private Stage stage;
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

        BorderPane borderPane = new BorderPane(scrollPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        borderPane.setLeft(listView);

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        listView.setOpacity(0);
        scrollPane.setOpacity(0);
        stage.setScene(scene);
        loadSceneTransition(0.5, listView, scrollPane);

        listView.getSelectionModel().selectedItemProperty().addListener(e -> {
            selectedListItem = listView.getSelectionModel().getSelectedItem();
        });
        listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                System.out.println("HERE");
                e.consume();
            }
        });
        scrollPane.setOnZoom(e -> {
            zooming(gridPane, e.getZoomFactor());
        });
    }

    private ListView<ListItem> getListViewPopulated() {
        ListView<ListItem> listView = new ListView();
        listView.getItems().add(new ListItem("Floor", new Image(this.getClass().getResource("/tiles/floor.jpg").toString())));
        listView.getItems().add(new ListItem("Wall", new Image(this.getClass().getResource("/tiles/wall.png").toString())));
        listView.getItems().add(new ListItem("Window", new Image(this.getClass().getResource("/tiles/window.png").toString())));
        listView.getItems().add(new ListItem("Door", new Image(this.getClass().getResource("/tiles/door.png").toString())));
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
        return new ArrayList<>();
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
