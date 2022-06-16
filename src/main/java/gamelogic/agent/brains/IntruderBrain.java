package gamelogic.agent.brains;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.deciders.TaskDeciderInterface;
import gamelogic.agent.tasks.deciders.TaskDeciderIntruder;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public class IntruderBrain implements BrainInterface {

    private final TaskContainer tasks;
    private TaskInterface currentTask;
    private TaskDeciderInterface taskDecider;

    public IntruderBrain (TaskContainer taskContainer, double angleSpawnToGoal) {
        this.tasks = taskContainer;
        //this.currentTask = tasks.getTask(TaskType.EXPLORATION_DIRECTION);
        this.taskDecider = tasks.getTaskDeciderIntruder();
        ((TaskDeciderIntruder)taskDecider).setTargetAngle(angleSpawnToGoal);
    }

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarkersDirectionIntruder, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells, List<Sound> guardYellsCaught) {
        currentTask = taskDecider.getTaskToPerform(graph, sounds, guardsSeen, intrudersSeen, currentTask);
        System.out.println("-- INTRUDER PERFORMS TASK " + currentTask.getType());
        
        return currentTask.performTask(graph, orientation, pheromoneMarkersDirectionIntruder, sounds, guardsSeen, intrudersSeen);
    }

    @Override
    public TaskInterface getTaskFromDecider(ExplorationGraph graph, double orientation, double pheromoneMarkersDirectionGuard, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells) {
        return taskDecider.getTaskToPerform(graph, sounds, guardsSeen, intrudersSeen, currentTask);
    }

    @Override
    public TaskType getCurrentTask() {
        return currentTask.getType();
    }

    @Override
    public String toString() {
        return "Intruder";
    }
}
