package gui.gamescreen;

import controller.Controller;
import controller.Vector2D;
import controller.maps.ScenarioMap;

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
            GAME_SCREEN.spawnAgent(agentsGuards[i], agentPositions[i]);
        }
    }

    @Override
    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        Vector2D oldPos = agentPositions[agentIndex];
        super.updateAgentPosition(agentIndex, pos);
        GAME_SCREEN.moveAgent(agentsGuards[agentIndex], oldPos, agentPositions[agentIndex]);
    }

    @Override
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        super.updateAgentOrientation(agentIndex, orientationToAdd);
        GAME_SCREEN.moveAgent(agentsGuards[agentIndex], agentPositions[agentIndex], agentPositions[agentIndex]);
    }

    @Override
    protected void updateProgress(Vector2D vector, int agentIndex) {
        super.updateProgress(vector, agentIndex);
        GAME_SCREEN.setProgress(endingExplorationMap.getCurrentTilesExplored(), endingExplorationMap.getTotalTilesToExplore());
        GAME_SCREEN.setToExplored(convertRelativeCurrentPosToAbsolute(vector, agentIndex));
    }
}
