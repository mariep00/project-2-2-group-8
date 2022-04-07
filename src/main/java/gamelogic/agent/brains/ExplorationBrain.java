package gamelogic.agent.brains;

import gamelogic.agent.BrainInterface;
import gamelogic.maps.graph.ExplorationGraph;

public class ExplorationBrain implements BrainInterface {

    //TODO Make this work with the old frontier brain. IMO we should make the FrontierBrain something like a "Explore Task", then each agent will have tasks it can do. This agent will then only have explore as a task
    @Override
    public int makeDecision(ExplorationGraph graph, double orientation) {
        return -1;
    }
}
