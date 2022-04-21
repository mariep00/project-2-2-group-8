package gamelogic.agent.brains;

import gamelogic.maps.graph.ExplorationGraph;

public interface BrainInterface {

    public int makeDecision (ExplorationGraph graph, double orientation, double pheromoneMarkersDirection);
}
