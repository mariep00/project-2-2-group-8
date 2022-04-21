package gamelogic.agent.tasks;

import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.Stack;

public interface TaskInterface {
    
    public Stack<Integer> performTask (ExplorationGraph graph, double orientation, double pheromoneMarkerDirection);
    public TaskType getType();
}
