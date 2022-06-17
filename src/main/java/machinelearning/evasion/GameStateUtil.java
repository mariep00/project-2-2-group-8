package machinelearning.evasion;

import datastructures.Vector2D;
import datastructures.quicksort.QuickSort;
import datastructures.quicksort.SortObject;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;
import gui.gamescreen.AgentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameStateUtil {
    public static double[] getVisionInput(VisionMemory[] visionMemories, VisionMemory visionEvadingFrom) {
        VisionMemory[] visionMemoryArray = Arrays.copyOf(visionMemories, visionMemories.length);
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

    public static double[] getWallsInput(ExplorationGraph graph) {
        int xClosest = 15;
        double[] wallsInput = new double[xClosest*2];
        Vector2D agentPosition = graph.getCurrentPosition().COORDINATES;
        LinkedList<Vector2D> walls = graph.getWalls();
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
                sortObjects[i] = new SortObject<>(wall, agentPosition.dist(wall));
            }
            SortObject<Vector2D>[] sortedObjects = quickSort.sort(sortObjects, 0, sortObjects.length-1);

            for (int i=0; i<xClosest; i++) {
                wallsInput[i*2] = agentPosition.getAngleBetweenVector(sortedObjects[i].object);
                wallsInput[(i*2)+1] = sortedObjects[i].sortParameter;
            }
        }
        return wallsInput;
    }

    public static double[] getSoundInput(List<Sound> sounds, Sound soundEvadingFrom) {
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

    public static double[] normalizeVisionInput(double[] visionInput) {
        double[] normalizedVisionInput = new double[visionInput.length];
        for (int i = 0; i < visionInput.length; i += 4) {
            normalizedVisionInput[i] = normalize(visionInput[i], 0, 360);
            normalizedVisionInput[i+1] = normalize(visionInput[i+1], 0, 100);
            normalizedVisionInput[i+2] = normalize(visionInput[i+2], 0, 81);
            normalizedVisionInput[i+3] = normalize(visionInput[i+3], 0, 1);
        }
        return normalizedVisionInput;
    }

    public static double[] normalizeWallsInput(double[] wallsInput) {
        double[] normalizedWallsInput = new double[wallsInput.length];
        for (int i = 0; i < wallsInput.length; i += 2) {
            normalizedWallsInput[i] = normalize(wallsInput[i], 0, 360);
            normalizedWallsInput[i+1] = normalize(wallsInput[i+1], 1, 60);
        }
        return normalizedWallsInput;
    }

    public static double[] normalizePheromoneMarkerInput(double[] pheromoneMarkerInput) {
        return new double[]{normalize(pheromoneMarkerInput[0], 0, 360)};
    }

    public static double[] normalizeSoundInput(double[] soundInput) {
        double[] normalizedSoundInput = new double[soundInput.length];
        for (int i = 0; i < soundInput.length; i += 2) {
            normalizedSoundInput[i] = normalize(soundInput[i], 0, 360);
            normalizedSoundInput[i+1] = normalize(soundInput[i+1], 0, 1);
        }
        return normalizedSoundInput;
    }

    public static double[] normalizeOrientationInput(double[] orientationInput) {
        return new double[]{normalize(orientationInput[0], 0, 270)};
    }

    // Normalize to the range [-1,1]
    private static double normalize(double value, double minValue, double maxValue) {
        return 2*((value - minValue) / (maxValue - minValue)) - 1;
    }

    public static int convertAgentType(AgentType agentType) {
        return agentType == AgentType.GUARD ? 0 : 1;
    }
}
