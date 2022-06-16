package gamelogic.controller.gamemodecontrollers;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.ScenarioMap;
import machinelearning.evasion.GameState;
import machinelearning.evasion.GameStateUtil;
import machinelearning.evasion.NetworkUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class ControllerSurveillanceRLEvasion extends ControllerSurveillance {
    public ControllerSurveillanceRLEvasion(ScenarioMap scenarioMap, EndingSurveillance endingCondition, TaskContainer taskContainer, int seed) {
        super(scenarioMap, endingCondition, taskContainer, seed);
    }

    public int tickIntruder(int agentIndex, int movementTask) {
        int reward = 0;
        super.tickAgent(agentIndex, movementTask);
        if (willBeAlive(agentIndex)) reward += 1;
        else reward -= 15;

        return reward;
    }

    private boolean willBeAlive(int agentIndex) {
        for (int i = 0; i < numberOfGuards; i++) {
            if (nextState.getAgentPosition(i).dist(nextState.getAgentPosition(agentIndex)) <= Math.sqrt(2)) {
                removeAgent(agentIndex);
                soundController.generateGuardYell2(i);
                return false;
            }
        }
        return true;
    }

    public GameState buildStateObservation(int agentIndex, Sound soundEvadingFrom, VisionMemory visionEvadingFrom, VisionMemory[] visionMemories, List<Sound> sounds, double phermoneMarkersDirection, boolean skip) {
        double[] normalizedData;
        // Check if the intruder is performing evasion, if not return an array of only -1
        // This is made s.t. when an intruder is not performing evasion all parameters are null
        if (visionMemories != null) {
            double[] visionInput = GameStateUtil.getVisionInput(visionMemories, visionEvadingFrom);
            double[] wallsInput = GameStateUtil.getWallsInput(agents[agentIndex].explorationGraph);
            double[] pheromoneInput = new double[]{phermoneMarkersDirection};
            double[] soundInput = GameStateUtil.getSoundInput(sounds, soundEvadingFrom);
            double[] orientationInput = new double[]{agents[agentIndex].getOrientation()};

            normalizedData = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(GameStateUtil.normalizeVisionInput(visionInput), GameStateUtil.normalizeWallsInput(wallsInput)),
                    GameStateUtil.normalizePheromoneMarkerInput(pheromoneInput)), GameStateUtil.normalizeSoundInput(soundInput)), GameStateUtil.normalizeOrientationInput(orientationInput));
        }
        else {
            normalizedData = new double[NetworkUtil.NUMBER_OF_INPUTS];
            Arrays.fill(normalizedData, -1);
        }
        return new GameState(normalizedData, skip);
    }
}
