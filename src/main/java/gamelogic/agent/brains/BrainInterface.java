package gamelogic.agent.brains;

import gamelogic.agent.tasks.deciders.TaskDeciderInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public interface BrainInterface {

    int makeDecision (ExplorationGraph graph, double orientation, double pheromoneMarkersDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells);
    boolean isPursuing();
}
