package gamelogic.controller.gamemodecontrollers;

import gamelogic.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.agent.brains.ExplorationBrain;
import gamelogic.controller.Controller;
import gamelogic.maps.ScenarioMap;

public class ControllerExploration extends Controller {
    public ControllerExploration(ScenarioMap scenarioMap) {
        super(scenarioMap);
    }

    @Override
    protected void initializeAgents() {
        int[] orientations = {0, 90, 180, 270};

        for (int i = 0; i < numberOfGuards; i++) {
            agents[i] = new Agent(scenarioMap.getBaseSpeedGuard(), 0.0, scenarioMap.getGuardViewAngle(),scenarioMap.getGuardViewRange(), orientations[rand.nextInt(orientations.length)], new ExplorationBrain());
        }
    }

    @Override
    protected void updateProgress() {
        for (int i = 0; i < numberOfGuards+numberOfIntruders; i++) {
            for (Vector2D vector : nextState.getVision(i)) {
                endingExploration.updateExplorationMap(convertRelativeCurrentPosToAbsolute(vector, i));
            }
        }
    }
}
