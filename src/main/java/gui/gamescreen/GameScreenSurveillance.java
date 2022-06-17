package gui.gamescreen;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.*;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FarPursuingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.EvasionTaskRL;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.maps.ScenarioMap;
import gui.EndingScreen;
import gui.gamescreen.controller.ControllerSurveillanceGUI;
import javafx.stage.Stage;

public class GameScreenSurveillance extends GameScreen {
    private final ControllerSurveillanceGUI controllerSurveillanceGUI;

    public GameScreenSurveillance(ScenarioMap scenarioMap) {
        super(scenarioMap);
        this.controllerSurveillanceGUI = new ControllerSurveillanceGUI(scenarioMap, new EndingSurveillance(scenarioMap), this, new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new FarPursuingTask(), new EvasionTaskRL(),
                new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()));
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

    @Override
    public void endScreen(){
        EndingScreen endingScreen = new EndingScreen(this, controllerSurveillanceGUI.getGameScreen().getStage().getScene(), controllerSurveillanceGUI.getGameScreen().getStage(), controllerSurveillanceGUI);

        controllerSurveillanceGUI.getControllerGUI().getMainController().end();
    }

    public void removeAgent(int agentIndex) {
        Vector2D agentPos = controllerSurveillanceGUI.getCurrentState().getAgentPosition(agentIndex);
        tiles[agentPos.y][agentPos.x].resetCharacterImage();
        removeVision(null, controllerSurveillanceGUI.getCurrentState().getVision(agentIndex));
    }
}
