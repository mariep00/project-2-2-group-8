package gui;

import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.agent.tasks.general.ExplorationTaskRandom;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.GameScreenExploration;
import gui.gamescreen.GameScreenSurveillance;
import gui.util.MainGUI;
import gui.util.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
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
    private ScenarioMenuField viewAngleIntruder;

    public ScenarioMenu(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Button buttonEditMap = new Button("Edit map");
        Button buttonExportMap = new Button("Export map");
        Button buttonPlayExploration = new Button("Play exploration");
        Button buttonPlaySurveillance = new Button("Play surveillance");

        HBox hBox = new HBox(25,buttonEditMap, buttonExportMap, buttonPlayExploration, buttonPlaySurveillance);

        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(hBox);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(8);
        gridPane.setHgap(15);

        TextField mapName = new TextField(scenarioMap.getMapName());
        mapName.getStyleClass().add("scenarioMenuMapName");
        mapName.setAlignment(Pos.CENTER);
        TextField fileName = new TextField(scenarioMap.getFileName());
        fileName.getStyleClass().add("scenarioMenuFileName");
        fileName.setAlignment(Pos.CENTER);

        ScenarioMenuField numberOfGuards = new ScenarioMenuField("Number of guards", scenarioMap.getNumGuards());
        ScenarioMenuField numberOfIntruders = new ScenarioMenuField("Number of intruders", scenarioMap.getNumIntruders());
        ScenarioMenuField baseSpeedGuard = new ScenarioMenuField("Base speed guard", scenarioMap.getBaseSpeedGuard());
        ScenarioMenuField baseSpeedIntruder = new ScenarioMenuField("Base speed intruder", scenarioMap.getBaseSpeedIntruder());
        ScenarioMenuField viewAngleGuard = new ScenarioMenuField("View angle guard", scenarioMap.getGuardViewAngle());
        ScenarioMenuField viewAngleIntruder = new ScenarioMenuField("View angle intruder", scenarioMap.getIntruderViewAngle());
        ScenarioMenuField viewRangeGuard = new ScenarioMenuField("View range guard", scenarioMap.getGuardViewRange());
        ScenarioMenuField viewRangeIntruder = new ScenarioMenuField("View range intruder", scenarioMap.getIntruderViewRange());
        ScenarioMenuDropdown explorationTask = new ScenarioMenuDropdown("Exploration task", new ExplorationTaskRandom(), new ExplorationTaskFrontier());

        ScenarioMenuField shadedReduction = new ScenarioMenuField("Shaded vision reduction in %", scenarioMap.getShadedReduction()*100);
        ScenarioMenuField footstepMaxDistance = new ScenarioMenuField("Footstep maximum hearing distance", scenarioMap.getFootstepMaxHearingDistance());
        ScenarioMenuField yellMaxDistance = new ScenarioMenuField("Yell maximum hearing distance", scenarioMap.getYellMaxHearingDistance());
        ScenarioMenuField soundStandardDeviation = new ScenarioMenuField("Sound standard deviation", scenarioMap.getSoundStandardDeviation());
        ScenarioMenuField pheromoneMaxDistance = new ScenarioMenuField("Pheromone marker maximum smelling distance", scenarioMap.getPheromoneMaxSmellingDistance());
        ScenarioMenuField pheromoneReduction = new ScenarioMenuField("Pheromone marker strength reduction per second", scenarioMap.getPheromoneReduction());

        ScenarioMenuField[] fields = {numberOfGuards, numberOfIntruders, baseSpeedGuard, baseSpeedIntruder, viewAngleGuard,
                viewAngleIntruder, viewRangeGuard, viewRangeIntruder, shadedReduction, footstepMaxDistance, yellMaxDistance,
                soundStandardDeviation, pheromoneMaxDistance, pheromoneReduction};

        gridPane.add(mapName, 0, 0, 4, 1);
        GridPane.setMargin(mapName, new Insets(25, 0, 0, 0));
        gridPane.add(fileName, 0, 1, 4, 1);
        GridPane.setMargin(fileName, new Insets(0, 0, 25, 0));

        gridPane.add(numberOfGuards.fieldDesription, 0, 2);
        gridPane.add(numberOfGuards.textField, 1, 2);
        gridPane.add(baseSpeedGuard.fieldDesription, 0, 3);
        gridPane.add(baseSpeedGuard.textField, 1, 3);
        gridPane.add(viewAngleGuard.fieldDesription, 0, 4);
        gridPane.add(viewAngleGuard.textField, 1, 4);
        gridPane.add(viewRangeGuard.fieldDesription, 0, 5);
        gridPane.add(viewRangeGuard.textField, 1, 5);
        GridPane.setHalignment(numberOfGuards.fieldDesription, HPos.RIGHT);
        GridPane.setHalignment(baseSpeedGuard.fieldDesription, HPos.RIGHT);
        GridPane.setHalignment(viewAngleGuard.fieldDesription, HPos.RIGHT);
        GridPane.setHalignment(viewRangeGuard.fieldDesription, HPos.RIGHT);

        gridPane.add(numberOfIntruders.fieldDesription, 2, 2);
        gridPane.add(numberOfIntruders.textField, 3, 2);
        gridPane.add(baseSpeedIntruder.fieldDesription, 2, 3);
        gridPane.add(baseSpeedIntruder.textField, 3, 3);
        gridPane.add(viewAngleIntruder.fieldDesription, 2, 4);
        gridPane.add(viewAngleIntruder.textField, 3, 4);
        gridPane.add(viewRangeIntruder.fieldDesription, 2, 5);
        gridPane.add(viewRangeIntruder.textField, 3, 5);

        gridPane.add(shadedReduction.fieldDesription, 0, 7);
        gridPane.add(shadedReduction.textField, 1, 7);
        gridPane.add(pheromoneMaxDistance.fieldDesription, 0, 8);
        gridPane.add(pheromoneMaxDistance.textField, 1, 8);
        gridPane.add(pheromoneReduction.fieldDesription, 0, 9);
        gridPane.add(pheromoneReduction.textField, 1, 9);
        GridPane.setHalignment(shadedReduction.fieldDesription, HPos.RIGHT);
        GridPane.setHalignment(pheromoneMaxDistance.fieldDesription, HPos.RIGHT);
        GridPane.setHalignment(pheromoneReduction.fieldDesription, HPos.RIGHT);

        gridPane.add(footstepMaxDistance.fieldDesription, 2, 7);
        gridPane.add(footstepMaxDistance.textField, 3, 7);
        gridPane.add(yellMaxDistance.fieldDesription, 2, 8);
        gridPane.add(yellMaxDistance.textField, 3, 8);
        gridPane.add(soundStandardDeviation.fieldDesription, 2, 9);
        gridPane.add(soundStandardDeviation.textField, 3, 9);

        GridPane.setMargin(footstepMaxDistance.textField, new Insets(40, 0, 0, 0));
        GridPane.setMargin(footstepMaxDistance.fieldDesription, new Insets(40, 0, 0, 0));
        GridPane.setMargin(shadedReduction.textField, new Insets(40, 0, 0, 0));
        GridPane.setMargin(shadedReduction.fieldDesription, new Insets(40, 0, 0, 0));

        gridPane.getColumnConstraints().addAll( new ColumnConstraints(300), new ColumnConstraints(100));
        gridPane.setGridLinesVisible( true );

        borderPane.setCenter(gridPane);
        gridPane.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER);
        BorderPane.setMargin(hBox, new Insets(0, 0, 30, 0));

        // TODO Add option to export map --> move from MapCreator
        // TODO Button to go back (back to LoadCreateMap or MapCreator, depending where user came from)
        // TODO Add fields to customize agent i.e. choose what algorithms agent should use

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(borderPane);

        buttonPlayExploration.setOnAction(e -> {
            updateScenarioMap(fields, mapName);
            quitSceneTransition(() -> new GameScreenExploration(scenarioMap).start(stage), borderPane);
        });
        buttonPlaySurveillance.setOnAction(e -> {
            updateScenarioMap(fields, mapName);
            quitSceneTransition(() -> new GameScreenSurveillance(scenarioMap).start(stage), borderPane);
        });
    }

    private void updateScenarioMap(ScenarioMenuField[] fields, TextField mapName) {
        scenarioMap.setMapName(mapName.getText());

        scenarioMap.setNumGuards(Integer.parseInt(fields[0].getText()));
        scenarioMap.setNumIntruders(Integer.parseInt(fields[1].getText()));
        scenarioMap.setBaseSpeedGuard(Double.parseDouble(fields[2].getText()));
        scenarioMap.setBaseSpeedIntruder(Double.parseDouble(fields[3].getText()));
        scenarioMap.setGuardViewAngle(Double.parseDouble(fields[4].getText()));
        scenarioMap.setIntruderViewAngle(Double.parseDouble(fields[5].getText()));
        scenarioMap.setGuardViewRange(Double.parseDouble(fields[6].getText()));
        scenarioMap.setIntruderViewRange(Double.parseDouble(fields[7].getText()));
        scenarioMap.setShadedReduction(Double.parseDouble(fields[8].getText())/100);
        scenarioMap.setFootstepMaxHearingDistance(Integer.parseInt(fields[9].getText()));
        scenarioMap.setYellMaxHearingDistance(Integer.parseInt(fields[10].getText()));
        scenarioMap.setSoundStandardDeviation(Double.parseDouble(fields[11].getText()));
        scenarioMap.setPheromoneMaxSmellingDistance(Integer.parseInt(fields[12].getText()));
        scenarioMap.setPheromoneReduction(Double.parseDouble(fields[13].getText()));
    }

    @Override
    public @NotNull List<Transition> getTransitions() {
        return transitions;
    }
}
