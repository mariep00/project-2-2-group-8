package gamelogic.controller.gamemodecontrollers;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.ScenarioMap;
import gamelogic.maps.graph.ExplorationGraph;
import machinelearning.evasion.GameState;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import datastructures.Vector2D;
import datastructures.quicksort.QuickSort;
import datastructures.quicksort.SortObject;

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
                return false;
            }
        }
        return true;
    }

    public GameState buildStateObservation(int agentIndex, Sound soundEvadingFrom, VisionMemory visionEvadingFrom, boolean skip) {
        double[] visionInput = getVisionInput(agentIndex, visionEvadingFrom);
        double[] wallsInput = getWallsInput(agentIndex);
        double[] pheromoneInput = getPheromoneMarkerInput(agentIndex);
        double[] soundInput = getSoundInput(agentIndex, soundEvadingFrom);
        double[] orientationInput = getOrientationInput(agentIndex);

        double[] mergedArray = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(visionInput, wallsInput), pheromoneInput), soundInput), orientationInput);
        double[] normalizedData = normalize(mergedArray);
        return new GameState(normalizedData, skip);
    }

    private double[] getVisionInput(int agentIndex, VisionMemory visionEvadingFrom) {
        VisionMemory[] visionMemoryArray = Arrays.copyOf(currentState.getAgentsSeen(agentIndex), numberOfGuards+numberOfIntruders);
        ArrayList<VisionMemory> visionMemoryList = new ArrayList<>(Arrays.stream(visionMemoryArray).toList());
        visionMemoryList.remove(visionEvadingFrom);

        double[] visionInput = new double[16];
        if (visionEvadingFrom != null) {
            visionInput[0] = visionEvadingFrom.position().angle();
            visionInput[1] = visionEvadingFrom.position().magnitude();
            visionInput[2] = visionEvadingFrom.secondsAgo();
            visionInput[3] = convertAgentType(visionEvadingFrom.agentType());
        }
        else {
            visionInput[0] = -1;
            visionInput[1] = -1;
            visionInput[2] = -1;
            visionInput[3] = -1;
        }


        visionMemoryList.sort((visionMemory1, visionMemory2) -> {
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

        for (int i = 0; i < 3; i++) {
            VisionMemory visionMemory = visionMemoryList.get(i);
            if (visionMemory != null) {
                visionInput[(i * 4)+4] = visionMemory.position().angle();
                visionInput[(1 + (i * 4))+4] = visionMemory.position().magnitude();
                visionInput[(2 + (i * 4))+4] = visionMemory.secondsAgo();
                visionInput[(3 + (i * 4))+4] = convertAgentType(visionMemory.agentType());
            }
            else {
                visionInput[(i * 4)+4] = -1;
                visionInput[(1 + (i * 4))+4] = -1;
                visionInput[(2 + (i * 4))+4] = -1;
                visionInput[(3 + (i * 4))+4] = -1;
            }
        }
        return visionInput;
    }

    private double[] getWallsInput(int agentIndex) {
        int xClosest = 15;
        double[] wallsInput = new double[xClosest*2];
        ExplorationGraph agentGraph = agents[agentIndex].explorationGraph;
        Vector2D agentPosition = agentGraph.getCurrentPosition().COORDINATES;
        LinkedList<Vector2D> walls = agentGraph.getWalls();
        if (walls.size()<xClosest) {
            for (int i=0; i<walls.size(); i++) {
                wallsInput[i*2] = agentPosition.getAngleBetweenVector(walls.get(i));
                wallsInput[(i*2)+1] = agentPosition.dist(walls.get(i));
            }
            for (int i=walls.size(); i<xClosest; i++) {
                wallsInput[i*2] = -1.0;
                wallsInput[(i*2)+1] = -1.0;
            }
        } else {
            SortObject<Vector2D>[] sortObjects = new SortObject[walls.size()];
            QuickSort<Vector2D> quickSort = new QuickSort<>();
            for (int i=0; i<walls.size(); i++) {
                Vector2D wall = walls.get(i);
                sortObjects[i] = new SortObject<Vector2D>(wall, agentPosition.dist(wall));
            }
            SortObject<Vector2D>[] sortedObjects = quickSort.sort(sortObjects, 0, sortObjects.length-1);

            for (int i=0; i<xClosest; i++) {
                wallsInput[i*2] = agentPosition.getAngleBetweenVector(sortedObjects[i].object);
                wallsInput[(i*2)+1] = sortedObjects[i].sortParameter;
            }
        }
        return wallsInput;
    }

    private double[] getPheromoneMarkerInput(int agentIndex) {
        return new double[]{-1}; // TODO Change to intruder pheromone markers
    }

    private double[] getSoundInput(int agentIndex, Sound soundEvadingFrom) {
        ArrayList<Sound> sounds = new ArrayList<>(soundController.getSoundDirections(agentIndex));
        double[] soundInput = new double[8];
        if (soundEvadingFrom != null) {
            soundInput[0] = soundEvadingFrom.angle();
            soundInput[1] = soundEvadingFrom.loudness();
        }
        else {
            soundInput[0] = -1;
            soundInput[1] = -1;
        }
        sounds.remove(soundEvadingFrom);
        sounds.sort((sound1, sound2) -> {
            if (sound1 == null && sound2 == null) {
                return 0;
            }
            if (sound1 == null) {
                return 1;
            }
            if (sound2 == null) {
                return -1;
            }
            return sound1.compareTo(sound2);
        });

        int count = 0;
        for (int i = sounds.size()-1; i >= sounds.size()-3; i--) {
            if (i >= 0) {
                soundInput[(count*2)+2] = sounds.get(i).angle();
                soundInput[(1+(count*2))+2] = sounds.get(i).loudness();
            }
            else {
                soundInput[(count*2)+2] = -1;
                soundInput[(1+(count*2))+2] = -1;
            }
            count++;
        }
        return soundInput;
    }

    private double[] getOrientationInput(int agentIndex) {
        return new double[]{agents[agentIndex].getOrientation()};
    }
    // Normalises between [-1,1]
    private double[] normalize(double[] data) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double x : data) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        double average = (min+max)/2.0;
        double range = (max-min)/2.0;
        for (int i=0; i<data.length; i++) {
            data[i] = (data[i] - average)/range;
        }
        return data;
    }
}
