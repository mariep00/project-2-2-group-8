package gamelogic.agent.tasks;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;

public interface TaskInterface {

    /**
     * Peform Task for a random task
     * @return Stack of type int which are certain movement tasks
     */
    default int performTask() {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

    /**
     * Perform task for a frontier based exploration task
     * @param graph Current map of the agent
     * @param orientation Orientation in which the agent is facing
     * @param pheromoneMarkerDirection Direction of pheromones
     * @return Stack of type int which are certain movement tasks
     */
    default int performTask (ExplorationGraph graph, double orientation, double pheromoneMarkerDirection) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

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
    default int performTask (ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

    /**
     * Perform task for other decisions
     * @param graph Current map of the agent
     * @param orientation Orientation in which agent is facing
     * @param intrudersSeen All intruders seen
     * @return Stack of type int which are certain movement tasks
     */
    default int performTask(ExplorationGraph graph, double orientation, VisionMemory[] intrudersSeen) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

    /**
     * Perform task for other decisions
     * @param graph Current map of the agent
     * @param orientation Orientation in which agent is facing
     * @return Stack of type int which are certain movement tasks
     */
    default int performTask(ExplorationGraph graph, double orientation) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

    default int performTask(ExplorationGraph graph, double orientation, LinkedList<Vector2D> path) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }


    default void setTarget(Sound target) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default void setTarget(VisionMemory target) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default void setTarget(Vector2D target) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default void setTarget(double angle) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default void setTarget(ExplorationGraph graph, double orientation, LinkedList<Vector2D> path) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default Object getTarget() {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default boolean isFinished() {
        return true;
    }
    default int getTickCount() {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default int getPriority() { return getType().priority; }
    TaskType getType();
    TaskInterface newInstance();
}
