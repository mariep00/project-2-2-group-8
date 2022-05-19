package gamelogic.agent.tasks.deciders;

import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public interface TaskDeciderInterface {
    default TaskInterface getTaskToPerform(ExplorationGraph graph, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells, TaskInterface currentTask, double orientation) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    default TaskInterface getTaskToPerform(ExplorationGraph graph, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, TaskInterface currentTask) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
    TaskDeciderInterface newInstance();

}
