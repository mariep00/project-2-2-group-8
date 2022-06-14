package gamelogic.agent.tasks.intruder;

import java.util.List;

import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

public class EvasionTaskML implements TaskInterface {

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        
        return 0;
    }

    @Override
    public TaskType getType() {
        return TaskType.INTRUDER_EVASION;
    }

    @Override
    public TaskInterface newInstance() {
        return new EvasionTaskML();
    }
    
}
