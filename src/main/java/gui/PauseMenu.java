package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PauseMenu {
    private Stage pauseStage;

    /**
     * Constructor to construct a pause menu
     * @param classObject   object which represents the class where the pause menu needs to be constructed for
     * @param scene         the scene where the pause scene needs to belong to
     * @param stage         the stage where the pause stage needs to belong to
     */
    public PauseMenu(Object classObject, Scene scene, Stage stage) {
        Label pauseTitle = new Label("Game paused");
        Button resumeButton = new Button("Resume game");
        Button mainMenuButton = new Button("Back to main menu");
        Button desktopButton = new Button("Quit to desktop");
        VBox vbox = new VBox(pauseTitle, resumeButton, mainMenuButton, desktopButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setId("pause_menu");

        Scene pauseScene = new Scene(vbox, Color.TRANSPARENT);
        pauseStage = new Stage(StageStyle.TRANSPARENT);
        pauseStage.setScene(pauseScene);
        pauseStage.initOwner(stage);

        pauseScene.getStylesheets().add(MainGUI.getStylesheet());

        scene.getRoot().setEffect(new GaussianBlur());
        pauseStage.show();
        resizeUiElements(scene, vbox, pauseStage, stage);

        pauseScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                closePauseMenu(scene);
                MainGUI.continueTransitions(classObject);
            }
        });
        resumeButton.setOnAction(e -> {
            closePauseMenu(scene);
            MainGUI.continueTransitions(classObject);
        });
        mainMenuButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to go back to the main menu?");
            ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Back to the main menu");
            alert.initOwner(stage);
            alert.setTitle("");
            alert.setHeaderText("");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    closePauseMenu(scene);
                    stage.close();
                    new HomeScreen(stage.getWidth(), stage.getHeight()).start(stage);
                }
            });
        });
        desktopButton.setOnAction(e -> MainGUI.quitToDesktopAlert(stage));

        stage.widthProperty().addListener(e2 -> resizeUiElements(scene, vbox, pauseStage, stage));
        stage.heightProperty().addListener(e2 -> resizeUiElements(scene, vbox, pauseStage, stage));
    }

    /**
     * Method which closes the pause menu
     * @param scene     The scene where the pause menu belongs to
     */
    public void closePauseMenu(Scene scene) {
        scene.getRoot().setEffect(null);
        pauseStage.close();
        MainGUI.closePauseMenu();
    }

    /**
     * Method that resizes all UI elements in the scene according to the current window size
     * @param scene         the scene where the pause menu belongs to
     * @param vbox          the vbox which contains all menu buttons
     * @param pauseStage    the stage where the pause menu belongs to
     * @param stage         the stage where the pause stage belongs to
     */
    private void resizeUiElements(Scene scene, VBox vbox, Stage pauseStage, Stage stage) {
        double windowTitleBarHeight = stage.getHeight()-scene.getHeight();
        pauseStage.setX(stage.getX());
        pauseStage.setY(stage.getY()+windowTitleBarHeight);
        pauseStage.setHeight(stage.getHeight()-windowTitleBarHeight);
        pauseStage.setWidth(stage.getWidth());
        vbox.setSpacing(stage.getHeight()/20);

        // Change the font size of the buttons
        double buttonFontSize = (stage.getWidth()/3.5+stage.getHeight()/10)/17;
        // Loop through every button, since they all need to have the same dimensions
        for (Node node : vbox.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button)node;
                button.setPrefWidth(stage.getWidth()/3.5);
                button.setPrefHeight(stage.getHeight()/10);
                button.setStyle("-fx-font-size:"+buttonFontSize+"px;");
            }
            else if (node instanceof  Label) {
                Label label = (Label)node;
                DoubleProperty labelFontSize = new SimpleDoubleProperty((stage.getWidth()/3.5+stage.getHeight()/10)/5);
                label.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpt;", labelFontSize));
            }
        }
    }
}
