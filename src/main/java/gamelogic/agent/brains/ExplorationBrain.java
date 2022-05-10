package gamelogic.agent.brains;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public class ExplorationBrain implements BrainInterface {
    private final TaskContainer tasks;
    private TaskInterface currentTask;

    public ExplorationBrain (TaskContainer taskContainer) {
        this.tasks = taskContainer;
        currentTask = tasks.getTask(TaskType.EXPLORATION);
    }

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells) {
        return currentTask.performTask(graph, orientation, pheromoneMarkerDirection);
    }
}
