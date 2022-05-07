package gamelogic.agent.brains;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public class GuardBrain implements BrainInterface {
    private final TaskContainer tasks;
    private TaskInterface currentTask;

    public GuardBrain (TaskContainer taskContainer) {
        this.tasks = taskContainer;
        currentTask = tasks.getTask(TaskType.EXPLORATION);
        //TODO: implement other tasks than exploration for guards and add them to the container
    }

    //TODO: Add logic for when to switch between different tasks in the container
    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        return -1;
    }
}
