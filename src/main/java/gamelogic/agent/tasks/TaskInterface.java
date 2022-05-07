package gamelogic.agent.tasks;

import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;
import java.util.Stack;

public interface TaskInterface {

    /**
     * Peform Task for a random task
     * @return Stack of type int which are certain movement tasks
     */
    Stack<Integer> performTask ();

    /**
     * Perform task for a frontier based exploration task
     * @param graph Current map of the agent
     * @param orientation Orientation in which the agent is facing
     * @param pheromoneMarkerDirection Direction of pheromones
     * @return Stack of type int which are certain movement tasks
     */
    Stack<Integer> performTask (ExplorationGraph graph, double orientation, double pheromoneMarkerDirection);

    /**
     * Perform task for other decisions
     * @param graph Current map of the agent
     * @param orientation Orientation in which agent is facing
     * @param pheromoneMarkerDirection Direction of pheromones
     * @param sounds Sounds the agent is percieving
     * @param guardsSeen All guards seen
     * @param intrudersSeen All intruders seen
     * @return Stack of type int which are certain movement tasks
     */
    Stack<Integer> performTask (ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen);

    TaskType getType();
    TaskInterface newInstance();
}
