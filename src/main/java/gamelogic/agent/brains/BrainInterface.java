package gamelogic.agent.brains;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public interface BrainInterface {

    default int makeDecision (ExplorationGraph graph, double orientation, double pheromoneMarkersDirectionGuard, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

    TaskInterface getTaskFromDecider(ExplorationGraph graph, double orientation, double pheromoneMarkersDirectionGuard, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells);
    TaskContainer.TaskType getCurrentTask();
}
