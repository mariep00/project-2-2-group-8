package gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

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
    }

    /**
     * Method that will show a dialog to confirm the closing of the application
     * @param stage     stage where the method is called from
     */
    public static void quitToDesktopAlert(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to quit to the desktop?");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Quit");
        alert.initOwner(stage);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    /**
     * Method to initialize the pause menu
     * @param classObject   object which represents the class where the sceneSetup method is called from
     * @param scene         scene which needs a pause menu
     * @param stage         stage that contains the scene which needs a pause menu
     */
    private static void initializePauseMenu(Object classObject, Scene scene, Stage stage) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (pauseMenu == null) {
                    pauseTransitions(classObject);
                    pauseMenu = new PauseMenu(classObject, scene, stage);
                }
                else {
                    pauseMenu.closePauseMenu(scene);
                    continueTransitions(classObject);
                }
            }
        });
    }

    /**
     * Method to close the pause menu
     */
    public static void closePauseMenu() {
        pauseMenu = null;
    }

    /**
     * Method to pause the transitions currently playing
     * @param classObject   object of the class where the pause menu is opened from
     */
    public static void pauseTransitions(Object classObject) {
        if (classObject instanceof HomeScreen) {
            ((HomeScreen) classObject).pauseTransitions();
        }
    }

    /**
     * Method to continue the transitions that were playing
     * @param classObject   object of the class where the pause menu is opened from
     */
    public static void continueTransitions(Object classObject) {
        if (classObject instanceof HomeScreen) {
            ((HomeScreen) classObject).continueTransitions();
        }
    }

    public static String getStylesheet() {
        return Objects.requireNonNull(MainGUI.class.getResource("/style.css")).toString();
    }
}
