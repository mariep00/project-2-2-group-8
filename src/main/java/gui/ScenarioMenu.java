package gui;

import controller.MapBuilder;
import gui.gamescreen.GameScreen;
import gui.mapcreator.MapCreator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScenarioMenu extends Application implements TransitionInterface {
    private Stage stage;
    private final ArrayList<Transition> transitions = new ArrayList<>();

    /**
     * Start method which is called by JavaFX
     *
     * @param stage the stage which is used to display the scene
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Button buttonCreateMap = new Button("Create map");
        Button buttonLoadMap = new Button("Load map");

        VBox vbox = new VBox(30, buttonLoadMap, buttonCreateMap);
        vbox.setAlignment(Pos.CENTER);

        buttonLoadMap.setPrefWidth(270);
        buttonLoadMap.setPrefHeight(80);
        buttonCreateMap.setPrefWidth(270);
        buttonCreateMap.setPrefHeight(80);

        buttonLoadMap.setStyle("-fx-font-size: " + 19 + "px;");
        buttonCreateMap.setStyle("-fx-font-size: " + 19 + "px;");

        BorderPane borderPane = new BorderPane(vbox);

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);

        vbox.setOpacity(0);

        stage.setScene(scene);
        loadSceneTransition( vbox);

        buttonLoadMap.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            System.out.println(selectedFile);
            if (selectedFile != null) quitSceneTransition(() -> new GameScreen(new MapBuilder(selectedFile).getMap()).start(stage), vbox);
        });
        buttonCreateMap.setOnAction(e -> quitSceneTransition(() -> new MapCreator().start(stage), vbox));
    }

    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }
}