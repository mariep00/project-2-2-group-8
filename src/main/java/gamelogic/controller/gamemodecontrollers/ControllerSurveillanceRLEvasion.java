package gamelogic.controller.gamemodecontrollers;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.ScenarioMap;
import machinelearning.evasion.GameState;
import machinelearning.evasion.GameStateUtil;

import java.util.Arrays;

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

    public GameState buildStateObservation(int agentIndex, double angle, boolean skip) {
        double[] visionInput = getVisionInput(agentIndex);
        double[] wallsInput = getWallsInput(agentIndex);
        return new GameState(new double[] {
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 0),
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 1),
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 3),
                GameStateUtil.getStateForDirection(agents[agentIndex], getScenarioMap().getMap(), currentState.getAgentPosition(agentIndex), angle, 4),
        }, skip);
    }

    private double[] getVisionInput(int agentIndex) {
        VisionMemory[] visionMemoryArray = Arrays.copyOf(currentState.getAgentsSeen(agentIndex), numberOfGuards+numberOfIntruders);
        Arrays.sort(visionMemoryArray, (visionMemory1, visionMemory2) -> {
            if (visionMemory1 == null && visionMemory2 == null) {
                return 0;
            }
            if (visionMemory1 == null) {
                return 1;
            }
            if (visionMemory2 == null) {
                return -1;
            }
            return visionMemory1.compareTo(visionMemory2);
        });

        VisionMemory[] bestThreeVisionMemory = Arrays.copyOfRange(visionMemoryArray, 0, 3);
        double[] visionInput = new double[9];
        for (int i = 0; i < bestThreeVisionMemory.length; i++) {
            VisionMemory visionMemory = bestThreeVisionMemory[i];
            if (visionMemory != null) {
                visionInput[i + (i * 3)] = visionMemory.position().angle();
                visionInput[i + (i * 3)] = visionMemory.position().magnitude();
                visionInput[i + (i * 3)] = visionMemory.secondsAgo();
            }
            else {
                visionInput[i + (i * 3)] = -1;
                visionInput[i + (i * 3)] = -1;
                visionInput[i + (i * 3)] = -1;
            }
        }
        return visionInput;
    }

    private double[] getWallsInput(int agentIndex) {
        return null;
    }
}
