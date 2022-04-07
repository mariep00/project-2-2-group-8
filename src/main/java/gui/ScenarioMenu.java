package gui;

import gamelogic.MapBuilder;
import gui.gamescreen.GameScreen;
import gui.mapcreator.MapCreator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScenarioMenu extends Application implements TransitionInterface {
    private Stage stage;
    private final ArrayList<Transition> transitions = new ArrayList<>();
    private final Scene sceneToUse;

    public ScenarioMenu(Scene sceneToUse) {
        this.sceneToUse = sceneToUse;
    }
    /**
     * Start method which is called by JavaFX
     *
     * @param stage the stage which is used to display the scene
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        ImageView logo;
        try {
            logo = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResource("/logo.png")).toString()));
        } catch (NullPointerException exception) {
            System.out.println("WARNING: Logo not found in the given directory.");
            logo = new ImageView();
        }
        logo.setPreserveRatio(true);
        logo.setFitWidth(400);
        logo.setFitHeight(400);
        Button buttonCreateMap = new Button("Create map");
        Button buttonLoadMap = new Button("Load map");

        VBox vboxButtons = new VBox(30, buttonLoadMap, buttonCreateMap);
        vboxButtons.setDisable(true); // To make sure you cannot click on buttons while transitions are playing
        vboxButtons.setMaxWidth(270);
        vboxButtons.setMaxHeight(175);

        VBox vboxLogo = new VBox(logo);
        vboxLogo.setAlignment(Pos.TOP_CENTER);
        VBox vboxCombined = new VBox(30, vboxLogo, vboxButtons);
        vboxCombined.setAlignment(Pos.CENTER);
        // Create a borderpane, with the logo centered in the top
        // and the buttons centered in the middle of the screen
        BorderPane borderPane = new BorderPane();
        //borderPane.setTop(vboxLogo);
        //BorderPane.setAlignment(vboxLogo, Pos.TOP_CENTER);
        //borderPane.setCenter(vboxButtons);
        vboxButtons.setAlignment(Pos.TOP_CENTER);
        borderPane.setCenter(vboxCombined);
        BorderPane.setAlignment(vboxCombined, Pos.CENTER);

        buttonLoadMap.setPrefWidth(270);
        buttonLoadMap.setPrefHeight(80);
        buttonCreateMap.setPrefWidth(270);
        buttonCreateMap.setPrefHeight(80);

        buttonLoadMap.setStyle("-fx-font-size: " + 19 + "px;");
        buttonCreateMap.setStyle("-fx-font-size: " + 19 + "px;");

        //Scene scene = new Scene(borderPane);
        //MainGUI.setupScene(this, scene, stage);

        vboxButtons.setOpacity(0);

        //stage.setScene(scene);
        sceneToUse.setRoot(borderPane);
        loadSceneTransition( vboxButtons);

        buttonLoadMap.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) quitSceneTransition(() -> new GameScreen(new MapBuilder(selectedFile).getMap()).start(stage), vboxButtons);
        });
        buttonCreateMap.setOnAction(e -> quitSceneTransition(() -> new MapCreator().start(stage), vboxButtons));
    }

    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }
}