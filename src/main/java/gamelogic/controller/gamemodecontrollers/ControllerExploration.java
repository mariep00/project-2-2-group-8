package gamelogic.controller.gamemodecontrollers;

import gamelogic.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.agent.brains.ExplorationBrain;
import gamelogic.controller.Controller;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.maps.ScenarioMap;

public class ControllerExploration extends Controller {
    private final EndingExploration endingExploration;

    public ControllerExploration(ScenarioMap scenarioMap, EndingExploration endingCondition) {
        super(scenarioMap, endingCondition);
        this.endingExploration = endingCondition;
    }

    @Override
    public void init() {
        super.init();
        updateProgress();
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
