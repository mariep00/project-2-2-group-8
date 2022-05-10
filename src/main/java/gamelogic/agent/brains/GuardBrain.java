package gamelogic.agent.brains;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.deciders.TaskDeciderInterface;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public class GuardBrain implements BrainInterface {
    private final TaskContainer tasks;
    private TaskInterface currentTask;
    private TaskDeciderInterface taskDecider;

    public GuardBrain (TaskContainer taskContainer) {
        this.tasks = taskContainer;
        this.currentTask = tasks.getTask(TaskType.EXPLORATION);
        this.taskDecider = tasks.getTaskDeciderGuard();
    }

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        TaskInterface taskToPerform = taskDecider.getTaskToPerform(graph, pheromoneMarkerDirection, sounds, guardsSeen, intrudersSeen, currentTask);
        // TODO make a .equals here for the task itself, while it could return same task type (checking sound), but checking a different sound
        if (taskToPerform.getType() != currentTask.getType()) currentTask = taskToPerform;

        return currentTask.performTask();
    }
}
