package gui;

import gui.gamescreen.GameScreen;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MainGUI {
    public static final int WINDOW_STARTING_WIDTH = 800;
    public static final int WINDOW_STARTING_HEIGHT = 600;

    private static PauseMenu pauseMenu;

    /**
     * Method to setup the similarities between scenes
     * @param scene         scene which needs to be setup
     */
    public static void setupScene(Object classObject, Scene scene, Stage stage) {
        scene.getStylesheets().add(getStylesheet());
        initializePauseMenu(classObject, scene, stage);
    }

    /**
     * Method to setup the similarities between stages
     * @param stage         stage that needs to be setup
     */
    public static void setupStage(Stage stage, double windowWidth, double windowHeight) {
        stage.setTitle("Some game name");
        stage.setMinHeight(WINDOW_STARTING_HEIGHT);
        stage.setMinWidth(WINDOW_STARTING_WIDTH);
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);

        stage.setOnCloseRequest(e -> System.exit(0));
    }

    /**
     * Method that will show a dialog to confirm the closing of the application
     * @param stage     stage where the method is called from
     */
    public static void quitToDesktopAlert(Stage stage) {
        Alert alert = getDefaultAlert(Alert.AlertType.CONFIRMATION);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Quit");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(0)).setText("Quit to desktop");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(1)).setText("Are you sure you want to quit to the desktop? " +
                "\nAny unsaved progress will be lost");
        alert.initOwner(stage);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    /**
     * Method that will show a dialog to confirm going back to the main menu
     * @param stage         stage where the method is called from
     * @param runnable      runnable that needs to be executed before going back to the main menu
     */
    public static void backToMainMenuAlert(Stage stage, @Nullable Runnable runnable) {
        Alert alert = getDefaultAlert(Alert.AlertType.CONFIRMATION);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Back to main menu");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(0)).setText("Back to main menu");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(1)).setText("Are you sure you want to go back to the main menu? " +
                "\nAny unsaved progress will be lost");
        alert.initOwner(stage);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (runnable != null) runnable.run();
                stage.close();
                new HomeScreen(stage.getWidth(), stage.getHeight()).start(stage);
            }
        });
    }

    /**
     * Method to get the default a dialog with the default style
     * @param type      the type of the alert
     * @return          the alert
     */

    private static Alert getDefaultAlert(Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.setGraphic(null);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setId("dialog_ok_button");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setPrefWidth(150);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setPrefHeight(30);
        alert.getDialogPane().setPrefWidth(425);
        alert.getDialogPane().setPrefHeight(175);

        GridPane gridPane = new GridPane();
        Label label1 = new Label();
        label1.setId("dialog_content_header");
        Label label2 = new Label();
        label2.setId("dialog_content");
        gridPane.add(label1, 0, 0);
        gridPane.add(label2, 0, 1);
        GridPane.setMargin(label1, new Insets(7, 10, 0, 10));
        GridPane.setMargin(label2, new Insets(15, 10, 0, 10));
        alert.getDialogPane().setContent(gridPane);

        return alert;
    }

    /**
     * Method to initialize the pause menu
     * @param classObject   object which represents the class where the sceneSetup method is called from
     * @param scene         scene which needs a pause menu
     * @param stage         stage that contains the scene which needs a pause menu
     */
    private static void initializePauseMenu(Object classObject, Scene scene, Stage stage) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (pauseMenu == null) {
                    if (classObject instanceof TransitionInterface) {
                        ((TransitionInterface) classObject).pauseTransitions(((TransitionInterface) classObject).getTransitions());
                    }
                    if (classObject instanceof GameScreen) {
                        ((GameScreen) classObject).pauseGame();
                    }
                    pauseMenu = new PauseMenu(classObject, scene, stage);
                }
            }
        });
    }

    /**
     * Method to close the pause menu
     */
    public static void closePauseMenu(Object classObject) {
        if (classObject instanceof TransitionInterface) {
            ((TransitionInterface) classObject).continueTransitions(((TransitionInterface) classObject).getTransitions());
        }
        if (classObject instanceof GameScreen) {
            ((GameScreen) classObject).continueGame();
        }
        pauseMenu = null;
    }

    /**
     * Method to get the stylesheet url string
     * @return      string with the path to the stylesheet
     */
    public static String getStylesheet() {
        return Objects.requireNonNull(MainGUI.class.getResource("/style.css")).toString();
    }
}
