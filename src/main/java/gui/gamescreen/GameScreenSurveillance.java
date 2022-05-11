package gui.gamescreen;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.ExplorationInDirection;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.agent.tasks.general.PathfindingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.PursuingTaskBaseline;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.controller.ControllerSurveillanceGUI;
import javafx.stage.Stage;

public class GameScreenSurveillance extends GameScreen {
    private final ControllerSurveillanceGUI controllerSurveillanceGUI;

    public GameScreenSurveillance(ScenarioMap scenarioMap) {
        super(scenarioMap);
        this.controllerSurveillanceGUI = new ControllerSurveillanceGUI(scenarioMap, new EndingSurveillance(scenarioMap), this, new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new PursuingTaskBaseline(), new EvasionTaskBaseline(),
                new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection()));
    }

    @Override
    public void start(Stage stage) {
        super.start(stage);
        controllerSurveillanceGUI.init();
    }

    @Override
    protected Tile getInitialFloorTile() { return new Tile(new TileImage(imageContainer.getFloor())); }

    @Override
    protected ControllerSurveillanceGUI getController() { return controllerSurveillanceGUI; }
}
