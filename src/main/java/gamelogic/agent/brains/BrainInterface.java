package gamelogic.agent.brains;

import gamelogic.datacarriers.Sound;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public interface BrainInterface {

    int makeDecision (ExplorationGraph graph, double orientation, double pheromoneMarkersDirection, List<Sound> sounds);
}
