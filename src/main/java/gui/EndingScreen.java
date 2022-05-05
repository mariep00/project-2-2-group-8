package gui;

import gui.util.MainGUI;
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

public class EndingScreen {
    private Stage endingStage;

    public EndingScreen (Object classObject, Scene scene, Stage stage){
        Label finishTitle = new Label("Game finished"); //TODO: change the colour
        finishTitle.setStyle("-fx-font-size: " + 55 + "px;");

        Button mainMenuButton = new Button("Back to main menu");
        Button desktopButton = new Button("Quit to desktop");
        VBox vboxButtons = new VBox(30, mainMenuButton, desktopButton);
        for (Node node : vboxButtons.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setPrefWidth(270);
                ((Button) node).setPrefHeight(80);
                node.setStyle("-fx-font-size: " + 19 + "px;");
            }
        }
        vboxButtons.setAlignment(Pos.CENTER); //TODO: position
        VBox vboxFinishTitle = new VBox(finishTitle);
        vboxFinishTitle.setAlignment(Pos.TOP_CENTER);
        VBox vboxCombined = new VBox(vboxFinishTitle, vboxButtons);
        vboxCombined.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane(vboxCombined);
        BorderPane.setAlignment(vboxCombined, Pos.CENTER);
        VBox.setMargin(vboxFinishTitle, new Insets(0, 0, 120, 0));

        borderPane.setId("pause_menu");
        Scene endingScene = new Scene(borderPane, Color.TRANSPARENT);
        endingStage = new Stage(StageStyle.TRANSPARENT);
        endingStage.setScene(endingScene);
        endingStage.initOwner(stage);

        double windowTitleBarHeight = stage.getHeight()-scene.getHeight();
        endingStage.setX(stage.getX());
        endingStage.setY(stage.getY()+windowTitleBarHeight);
        endingStage.setHeight(stage.getHeight()-windowTitleBarHeight);
        endingStage.setWidth(stage.getWidth());

        endingScene.getStylesheets().add(MainGUI.getStylesheet());
        endingStage.widthProperty().add(stage.widthProperty());
        endingStage.heightProperty().add(stage.heightProperty());
        scene.getRoot().setEffect(new GaussianBlur());

        endingStage.show();

        stage.widthProperty().addListener(e -> {
            endingStage.setX(stage.getX());
            endingStage.setWidth(stage.getWidth());
        });

        stage.heightProperty().addListener(e -> {
            endingStage.setY(stage.getY()+windowTitleBarHeight);
            endingStage.setHeight(stage.getHeight()-windowTitleBarHeight);
        });

        //TODO: pause scene appears when game is finished

        mainMenuButton.setOnAction(e -> MainGUI.backToMainMenuAlert(stage,() -> closePauseMenu(scene, classObject)));
        desktopButton.setOnAction(e -> MainGUI.quitToDesktopAlert(stage));
    }
    public void closePauseMenu(Scene scene, Object classObject) {
        scene.getRoot().setEffect(null);
        endingStage.close();
        MainGUI.closePauseMenu(classObject);
    }
}
