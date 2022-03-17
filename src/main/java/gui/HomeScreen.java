package gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreen extends Application implements TransitionInterface {
    private Stage stage;
    private final ArrayList<Transition> transitions = new ArrayList<>();
    private final double windowWidth;
    private final double windowHeight;

    /**
     * Constructor which is used to create a home screen using a different window size
     * @param windowWidth   the window width of the home screen
     * @param windowHeight  the window height of the home screen
     */
    public HomeScreen(double windowWidth, double windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    /**
     * Constructor which uses the predefined window size, only used when launching the application
     */
    public HomeScreen() {
        this.windowWidth = MainGUI.WINDOW_STARTING_WIDTH;
        this.windowHeight = MainGUI.WINDOW_STARTING_HEIGHT;
    }

    /**
     * Start method which is called by JavaFX
     *
     * @param stage the stage which is used to display the scene
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Image icon = new Image(MainGUI.class.getResource("/cam.png").toString());
        stage.getIcons().add(icon);
        try {
            // Adding the icon to the dock on MacOS
            BufferedImage iconMac = SwingFXUtils.fromFXImage(icon, null);
            Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(iconMac);
        } catch (Exception exception) {}
        loadHomeScreen();
    }
    /**
     * Method to load the home screen
     */
    public void loadHomeScreen() {
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

        Button playButton = new Button("Play game");
        Button quitButton = new Button("Quit game");
        VBox vboxButtons = new VBox( 30,playButton, quitButton);
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

        playButton.setPrefWidth(270);
        playButton.setPrefHeight(80);
        quitButton.setPrefWidth(270);
        quitButton.setPrefHeight(80);

        playButton.setStyle("-fx-font-size: " + 19 + "px;");
        quitButton.setStyle("-fx-font-size: " + 19 + "px;");

        // Set the opacities to 0, so they can fade in using the animation
        logo.setOpacity(0);
        vboxButtons.setOpacity(0);
        Scene scene = new Scene(borderPane, windowWidth, windowHeight);
        MainGUI.setupStage(stage, windowWidth, windowHeight);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);

        // Initially the stage should not be resizable, because of issues with resizing and playing animations at the same time
        stage.setResizable(false);

        stage.show();
        loadHomeScreenTransition(vboxButtons, logo);

        playButton.setOnAction(e -> quitSceneTransition(() -> new ScenarioMenu(scene).start(stage), vboxButtons));
        quitButton.setOnAction(e -> MainGUI.quitToDesktopAlert(stage));
    }

    /**
     * This method creates and executes the transitions for the logo and buttons
     *
     * @param vbox VBox that contains the two buttons
     * @param logo ImageView that contains the logo
     */
    private void loadHomeScreenTransition(VBox vbox, ImageView logo) {
        // Create the path transition for the logo
        Path logoPath = new Path();
        MoveTo moveFrom = new MoveTo(logo.getX() + logo.getFitWidth() / 2, -logo.getFitHeight());
        logoPath.getElements().add(moveFrom);
        logoPath.getElements().add(new LineTo(logo.getFitWidth() / 2, 241.26983642578125/2));
        PathTransition pathTransitionLogo = new PathTransition(Duration.seconds(1.3), logoPath);

        // Create the fade in transition for the logo
        FadeTransition fadeTransitionLogo = new FadeTransition(Duration.seconds(2));
        fadeTransitionLogo.setFromValue(0);
        fadeTransitionLogo.setToValue(1);

        // Combine these two transitions to one transition
        ParallelTransition parallelTransitionLogo = new ParallelTransition(logo, pathTransitionLogo, fadeTransitionLogo);
        transitions.add(parallelTransitionLogo);
        parallelTransitionLogo.play();
        // When the logo transitions are done, we want to execute the buttons transitions
        parallelTransitionLogo.setOnFinished(e -> {
            transitions.remove(parallelTransitionLogo);
            // First we create the fade in transition for the buttons
            FadeTransition fadeTransitionButtons = new FadeTransition(Duration.seconds(1));
            fadeTransitionButtons.setFromValue(0);
            fadeTransitionButtons.setToValue(1);

            // Second we create the scale transition for the buttons
            ScaleTransition scaleTransitionButtons = new ScaleTransition(Duration.seconds(0.9));
            scaleTransitionButtons.setFromX(0.4);
            scaleTransitionButtons.setFromY(0.4);
            scaleTransitionButtons.setToX(1);
            scaleTransitionButtons.setToY(1);

            // Combine these two transitions to one transition
            ParallelTransition parallelTransitionButtons = new ParallelTransition(vbox, fadeTransitionButtons, scaleTransitionButtons);
            transitions.add(parallelTransitionButtons);
            parallelTransitionButtons.play();

            // When the transitions are done we want to enable window resizing again
            parallelTransitionButtons.setOnFinished(e2 -> {
                stage.setResizable(true);
                transitions.remove(parallelTransitionButtons);
                vbox.setDisable(false);
                System.out.println(logo.getTranslateX() + ", " + logo.getTranslateY());
            });
        });
    }

    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
