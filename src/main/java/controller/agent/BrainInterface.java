package controller.agent;

import controller.maps.graph.ExplorationGraph;

public interface BrainInterface {
    
    public int makeDecision (ExplorationGraph graph, double orientation);
}
