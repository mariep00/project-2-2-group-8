package gamelogic.agent.brains;

import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public interface BrainInterface {

    int makeDecision (ExplorationGraph graph, double orientation, double pheromoneMarkersDirection, List<Double> soundDirections);
}
