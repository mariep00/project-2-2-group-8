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
        this.taskDecider = tasks.getTaskDeciderIntruder();
    }

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells) {
        currentTask = taskDecider.getTaskToPerform(graph, sounds, guardsSeen, intrudersSeen, currentTask);
        System.out.println("INTRUDER PERFORMS TASK " + currentTask.getType());
        // TODO Add intruder pheromone markers
        return currentTask.performTask(graph, orientation, 0,sounds, guardsSeen, intrudersSeen);
    }

    @Override
    public String toString() {
        return "Intruder";
    }
}
