package gui;

import gamelogic.controller.Controller;
import gui.gamescreen.GameScreenExploration;
import gui.gamescreen.controller.ControllerGUI;
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

    public EndingScreen (Object classObject, Scene scene, Stage stage, Controller controller){
        Label finishTitle = new Label("Game finished");
        finishTitle.setStyle("-fx-font-size: " + 55 + "px;");
        finishTitle.setTextFill(Color.web("#4c72d0"));
        int hours = (int) controller.time / 3600;
        int minutes = ((int)controller.time % 3600) / 60;
        double seconds = Math.round(controller.time % 60);
        int steps = (int) (controller.time/controller.getTimestep());
        int nbintruders = controller.getNumberOfIntruders();


        Label time = new Label("Time taken: " + hours + " hour(s) " + minutes + " minutes " + seconds + " seconds");
        time.setStyle("-fx-font-size: " + 55 + "px;");
        time.setTextFill(Color.WHITE);

        Button mainMenuButton = new Button("Back to main menu");
        Button desktopButton = new Button("Quit to desktop");
        VBox vboxButtons;

        if(!controller.getEndingCondition().mode()) //mode is surveillance default mode (True) is exploration
        {
            Label won = nbintruders==0?new Label("The guards won!!"): new Label("The intruders won");
            won.setStyle("-fx-font-size: " + 55 + "px;");
            won.setTextFill(Color.WHITE);
            vboxButtons = new VBox(30, won, time, mainMenuButton, desktopButton);

            if (nbintruders!=0) {
                Label intrudersnb = new Label("The number of intruders at the end:" + nbintruders);
                won.setStyle("-fx-font-size: " + 55 + "px;");
                won.setTextFill(Color.WHITE);
                vboxButtons = new VBox(30, won, time, intrudersnb, mainMenuButton, desktopButton);
            }
        }

        else{ //mode is exploration
            Label step = new Label("Steps taken: " + steps);
            step.setStyle("-fx-font-size: " + 55 + "px;");
            step.setTextFill(Color.WHITE);
            vboxButtons = new VBox(30, time, step, mainMenuButton, desktopButton);
        }

        for (Node node : vboxButtons.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setPrefWidth(270);
                ((Button) node).setPrefHeight(80);
                node.setStyle("-fx-font-size: " + 19 + "px;");
            }
        }
        vboxButtons.setAlignment(Pos.CENTER);
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

        mainMenuButton.setOnAction(e -> MainGUI.backToMainMenuAlert(stage,() -> closePauseMenu(scene, classObject)));
        desktopButton.setOnAction(e -> MainGUI.quitToDesktopAlert(stage));
    }


    public void closePauseMenu(Scene scene, Object classObject) {
        scene.getRoot().setEffect(null);
        endingStage.close();
        MainGUI.closePauseMenu(classObject);
    }
}
