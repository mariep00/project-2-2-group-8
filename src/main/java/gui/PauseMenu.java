package gui;

import gui.utils.MainGUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
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
        pauseTitle.setStyle("-fx-font-size: " + 55 + "px;");

        Button resumeButton = new Button("Resume game");
        Button mainMenuButton = new Button("Back to main menu");
        Button desktopButton = new Button("Quit to desktop");
        VBox vboxButtons = new VBox(30, resumeButton, mainMenuButton, desktopButton);
        for (Node node : vboxButtons.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setPrefWidth(270);
                ((Button) node).setPrefHeight(80);
                node.setStyle("-fx-font-size: " + 19 + "px;");
            }
        }
        vboxButtons.setAlignment(Pos.CENTER);

        VBox vboxPauseTitle = new VBox(pauseTitle);
        vboxPauseTitle.setAlignment(Pos.TOP_CENTER);
        VBox vboxCombined = new VBox(vboxPauseTitle, vboxButtons);
        vboxCombined.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane(vboxCombined);
        BorderPane.setAlignment(vboxCombined, Pos.CENTER);
        VBox.setMargin(vboxPauseTitle, new Insets(0, 0, 120, 0));

        borderPane.setId("pause_menu");
        Scene pauseScene = new Scene(borderPane, Color.TRANSPARENT);
        pauseStage = new Stage(StageStyle.TRANSPARENT);
        pauseStage.setScene(pauseScene);
        pauseStage.initOwner(stage);

        double windowTitleBarHeight = stage.getHeight()-scene.getHeight();
        pauseStage.setX(stage.getX());
        pauseStage.setY(stage.getY()+windowTitleBarHeight);
        pauseStage.setHeight(stage.getHeight()-windowTitleBarHeight);
        pauseStage.setWidth(stage.getWidth());

        pauseScene.getStylesheets().add(MainGUI.getStylesheet());

        pauseStage.widthProperty().add(stage.widthProperty());
        pauseStage.heightProperty().add(stage.heightProperty());
        scene.getRoot().setEffect(new GaussianBlur());

        pauseStage.show();

        stage.widthProperty().addListener(e -> {
            pauseStage.setX(stage.getX());
            pauseStage.setWidth(stage.getWidth());
        });
        stage.heightProperty().addListener(e -> {
            pauseStage.setY(stage.getY()+windowTitleBarHeight);
            pauseStage.setHeight(stage.getHeight()-windowTitleBarHeight);
        });

        pauseScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                closePauseMenu(scene, classObject);
            }
        });
        resumeButton.setOnAction(e -> closePauseMenu(scene, classObject));
        mainMenuButton.setOnAction(e -> MainGUI.backToMainMenuAlert(stage,() -> closePauseMenu(scene, classObject)));
        desktopButton.setOnAction(e -> MainGUI.quitToDesktopAlert(stage));
    }

    /**
     * Method which closes the pause menu
     * @param scene     The scene where the pause menu belongs to
     */
    public void closePauseMenu(Scene scene, Object classObject) {
        scene.getRoot().setEffect(null);
        pauseStage.close();
        MainGUI.closePauseMenu(classObject);
    }
}
