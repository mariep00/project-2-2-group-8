package controller.agent;

import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.Node;

public class AdvancedBrain implements BrainInterface {

    private ExplorationGraph graph;
    private int[] decisions;
    private int currentIndex;

    public AdvancedBrain () {
        decisions = new int[1];
        currentIndex = -1;
    }

    @Override
    public int makeDecision(ExplorationGraph graph) {
        this.graph = graph;
        if (currentIndex==decisions.length || currentIndex==-1) {
            Node frontier = getClosestFrontier();
            decisions = moveToPosition(frontier);
            currentIndex = 0;
        }
        
        int decision = decisions[currentIndex];
        currentIndex++;
        return decision;
    }

    private Node getClosestFrontier () {
        return null;

    }

    private int[] moveToPosition (Node destination) {



        return null;
    }
    
}
