package gamelogic.controller.gamemodecontrollers;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.maps.ScenarioMap;
import machinelearning.evasion.GameState;
import machinelearning.evasion.GameStateUtil;

public class ControllerSurveillanceRLEvasion extends ControllerSurveillance {
    public ControllerSurveillanceRLEvasion(ScenarioMap scenarioMap, EndingSurveillance endingCondition, TaskContainer taskContainer, int seed) {
        super(scenarioMap, endingCondition, taskContainer, seed);
    }

    public int tickIntruder(int agentIndex, int movementTask) {
        int reward = 0;
        super.tickAgent(agentIndex, movementTask);
        // Check if agent finished evasion
        if (agents[agentIndex].getCurrentTask() != TaskContainer.TaskType.INTRUDER_EVASION) {
            reward += 15; // TODO This reward won't work
        }
        if (willBeAlive(agentIndex)) reward += 1;
        else reward -= 15;

        return reward;
    }

    private boolean willBeAlive(int agentIndex) {
        for (int i = 0; i < numberOfGuards; i++) {
            if (nextState.getAgentPosition(i).dist(nextState.getAgentPosition(agentIndex)) <= Math.sqrt(2)) {
                return false;
            }
        }
        return true;
    }
    /*
    @Override
    public void tickAgent(int agentIndex, int movementTask) {
        // Check if guard is doing evasion
        if (agentIndex >= numberOfGuards && agents[agentIndex].getCurrentTask() == TaskContainer.TaskType.INTRUDER_EVASION) {
            super.tickAgent(agentIndex);
            // Check if agent finished evasion
            if (agents[agentIndex].getCurrentTask() != TaskContainer.TaskType.INTRUDER_EVASION) {
                reward += 15;
            }
        }
        else super.tickAgent(agentIndex, movementTask);
    }
     */

    /*
    @Override
    protected void tickAgents() {
        boolean[] intrudersAlive = new boolean[numberOfIntruders];
        for (int i = numberOfGuards; i < numberOfGuards+numberOfIntruders; i++) {
            if (agents[i] != null && agents[i].getCurrentTask() == TaskContainer.TaskType.INTRUDER_EVASION) intrudersAlive[i] = true;
        }
        super.tickAgents();
        boolean intruderAlive = false;
        for (int i = 0; i < numberOfIntruders; i++) {
            if (intrudersAlive[i]) { // Was alive before
                if (agents[i+numberOfGuards] == null) reward -= 10; // Died doing evasion
                else {
                    intruderAlive = true;
                    reward += 1; // Still alive doing evasion
                }
            }
        }
        if (!intruderAlive) reward -= 25; // All intruders got caught
    }*/

    public GameState buildStateObservation(int agentIndex, double angle, boolean skip) {
        return new GameState(new double[] {
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 0),
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 1),
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 3),
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 4),
        }, skip);
    }

    /*
    public int getReward() {
        int temp = reward;
        reward = 0;
        return temp;
    }*/
}
