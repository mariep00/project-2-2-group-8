package gamelogic.agent.brains;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.deciders.TaskDeciderInterface;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public class IntruderBrain implements BrainInterface {

    private final TaskContainer tasks;
    private TaskInterface currentTask;
    private TaskDeciderInterface taskDecider;

    public IntruderBrain (TaskContainer taskContainer) {
        this.tasks = taskContainer;
        this.currentTask = tasks.getTask(TaskType.EXPLORATION_DIRECTION);
        this.taskDecider = tasks.getTaskDeciderGuard();
    }

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarker, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        TaskInterface taskToPerform = taskDecider.getTaskToPerform(graph, pheromoneMarker, sounds, guardsSeen, intrudersSeen, currentTask);
        if (!taskToPerform.equals(currentTask)) currentTask = taskToPerform;

        return currentTask.performTask();
    }
}
