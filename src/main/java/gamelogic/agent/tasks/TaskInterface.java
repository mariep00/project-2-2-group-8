package gamelogic.agent.tasks;

import java.util.Stack;

import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.maps.graph.ExplorationGraph;

public interface TaskInterface {
    
    public Stack<Integer> performTask (ExplorationGraph graph, double orientation);
    public TaskType getType();
}
