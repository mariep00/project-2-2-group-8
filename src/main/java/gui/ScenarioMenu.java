package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ScenarioMenu extends Application {
    private Stage stage;

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
        HBox hBox = new HBox();
        hBox.getChildren().addAll(buttonCreateMap, buttonLoadMap);
        hBox.setSpacing(100);
        hBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(hBox);

        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);

        buttonLoadMap.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            // Load game with the selected file
        });
    }
}
