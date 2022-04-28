package gui;

import gamelogic.maps.ScenarioMap;
import gui.gamescreen.GameScreenExploration;
import gui.gamescreen.GameScreenSurveillance;
import gui.util.MainGUI;
import gui.util.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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

        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(hBox);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(8);
        gridPane.setHgap(15);

        TextField mapName = new TextField(scenarioMap.getName());
        mapName.getStyleClass().add("scenarioMenuTitle");
        mapName.setAlignment(Pos.CENTER);

        ScenarioMenuField numberOfGuards = new ScenarioMenuField("Number of guards", scenarioMap.getNumGuards());
        ScenarioMenuField numberOfIntruders = new ScenarioMenuField("Number of intruders", scenarioMap.getNumIntruders());
        ScenarioMenuField baseSpeedGuard = new ScenarioMenuField("Base speed guard", scenarioMap.getBaseSpeedGuard());
        ScenarioMenuField baseSpeedIntruder = new ScenarioMenuField("Base speed intruder", scenarioMap.getBaseSpeedIntruder());
        ScenarioMenuField viewAngleGuard = new ScenarioMenuField("View angle guard", scenarioMap.getGuardViewAngle());
        ScenarioMenuField viewAngleIntruder = new ScenarioMenuField("View angle intruder", scenarioMap.getIntruderViewAngle());
        ScenarioMenuField viewRangeGuard = new ScenarioMenuField("View range guard", scenarioMap.getGuardViewRange());
        ScenarioMenuField viewRangeIntruder = new ScenarioMenuField("View range intruder", scenarioMap.getIntruderViewRange());


        gridPane.add(mapName, 0, 0, 2, 1);
        gridPane.setMargin(mapName, new Insets(30, 0, 30, 0));
        gridPane.add(numberOfGuards, 0, 1);
        gridPane.add(numberOfIntruders, 1, 1);
        gridPane.add(baseSpeedGuard, 0, 2);
        gridPane.add(baseSpeedIntruder, 1, 2);
        gridPane.add(viewAngleGuard, 0, 3);
        gridPane.add(viewAngleIntruder, 1, 3);
        gridPane.add(viewRangeGuard, 0, 4);
        gridPane.add(viewRangeIntruder, 1, 4);


        borderPane.setCenter(gridPane);
        gridPane.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        BorderPane.setAlignment(hBox, Pos.CENTER_RIGHT);
        // TODO Add fields with details about the map (view range, speed, number of guards etc. etc.) and make them changeable, also show the title of the map or file name
        // TODO Add option to export map --> move from MapCreator
        // TODO Button to go back (back to LoadCreateMap or MapCreator, depending where user came from)
        // TODO Add fields to customize agent i.e. choose what algorithms agent should use

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(borderPane);

        buttonPlayExploration.setOnAction(e -> quitSceneTransition(() -> new GameScreenExploration(scenarioMap).start(stage), borderPane));
        buttonPlaySurveillance.setOnAction(e -> quitSceneTransition(() -> new GameScreenSurveillance(scenarioMap).start(stage), borderPane));
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
