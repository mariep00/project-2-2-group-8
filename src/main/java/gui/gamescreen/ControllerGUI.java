package gui.gamescreen;

import controller.Controller;
import controller.Vector2D;
import controller.agent.Agent;
import controller.maps.ScenarioMap;

import java.util.ArrayList;

public class ControllerGUI extends Controller {
    private final GameScreen GAME_SCREEN;
    public ControllerGUI(ScenarioMap scMap, GameScreen gameScreen) {
        super(scMap);
        this.GAME_SCREEN = gameScreen;
    }

    @Override
    protected void spawnAgents() {
        super.spawnAgents();
        for (int i = 0; i < agentsGuards.length; i++) {
            GAME_SCREEN.spawnAgent(i, agentPositions[i]);
        }
    }

    @Override
    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        Vector2D oldPos = agentPositions[agentIndex];
        super.updateAgentPosition(agentIndex, pos);
        GAME_SCREEN.moveAgent(agentIndex, oldPos, agentPositions[agentIndex]);
    }

    @Override
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        super.updateAgentOrientation(agentIndex, orientationToAdd);
        GAME_SCREEN.moveAgent(agentIndex, agentPositions[agentIndex], agentPositions[agentIndex]);
    }

    @Override
    protected boolean updateProgress(Vector2D vector, int agentIndex) {
        boolean toReturn = super.updateProgress(vector, agentIndex);
        GAME_SCREEN.setProgress(endingExplorationMap.getCurrentTilesExplored(), endingExplorationMap.getTotalTilesToExplore());
        GAME_SCREEN.setToExplored(convertRelativeCurrentPosToAbsolute(vector, agentIndex));
        return toReturn;
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < agentsGuards.length; i++) {
            updateAgentVision(i, calculateFOV(i, agentPositions[i]));
        }
    }

    @Override
    protected void updateAgent(int agentIndex, int task) {
        super.updateAgent(agentIndex, task);
        ArrayList<Vector2D> positionsInVision = calculateFOV(agentIndex, agentPositions[agentIndex]);
        updateAgentVision(agentIndex, positionsInVision);
        for (Vector2D vector : positionsInVision) {
            updateProgress(vector, agentIndex);
        }
    }

    public Agent getAgent(int index) { return agentsGuards[index]; }

    private void updateAgentVision(int agentIndex, ArrayList<Vector2D> positionsInVision) {
        GAME_SCREEN.updateVision(agentIndex, convertRelativeCurrentPosToAbsolute(positionsInVision, agentIndex));
    }

    public void hideVision(int agentIndex) {
        GAME_SCREEN.removeVision(agentIndex, convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPositions[agentIndex]), agentIndex));
    }
    public void showVision(int agentIndex) {
        GAME_SCREEN.showVision(convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPositions[agentIndex]), agentIndex));
    }
}
