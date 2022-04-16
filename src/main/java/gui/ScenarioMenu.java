package gui;

import gamelogic.maps.ScenarioMap;
import gui.gamescreen.GameScreenExploration;
import gui.gamescreen.GameScreenSurveillance;
import gui.util.MainGUI;
import gui.util.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScenarioMenu extends Application implements TransitionInterface {
    private final ScenarioMap scenarioMap;
    private Stage stage;
    private final ArrayList<Transition> transitions = new ArrayList<>();

    public ScenarioMenu(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Button buttonPlayExploration = new Button("Play exploration");
        Button buttonPlaySurveillance = new Button("Play surveillance");

        HBox hBox = new HBox(buttonPlayExploration, buttonPlaySurveillance);

        // TODO Add fields with details about the map (view range, speed, number of guards etc. etc.) and make them changeable, also show the title of the map or file name
        // TODO Add option to export map --> move from MapCreator
        // TODO Button to go back (back to LoadCreateMap or MapCreator, depending where user came from)
        // TODO Add fields to customize agent i.e. choose what algorithms agent should use

        Scene scene = new Scene(hBox);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(hBox);

        buttonPlayExploration.setOnAction(e -> quitSceneTransition(() -> new GameScreenExploration(scenarioMap).start(stage), hBox));
        buttonPlaySurveillance.setOnAction(e -> quitSceneTransition(() -> new GameScreenSurveillance(scenarioMap).start(stage), hBox));
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
