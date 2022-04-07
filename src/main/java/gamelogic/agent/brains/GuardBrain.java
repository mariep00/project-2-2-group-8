package gamelogic.agent.brains;

import gamelogic.agent.BrainInterface;
import gamelogic.maps.graph.ExplorationGraph;

public class GuardBrain implements BrainInterface {

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation) {
        return -1;
    }
}
