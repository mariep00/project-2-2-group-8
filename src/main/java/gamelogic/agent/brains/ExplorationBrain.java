package gamelogic.agent.brains;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;
import java.util.Stack;

public class ExplorationBrain implements BrainInterface {
    private final TaskContainer tasks;
    private TaskInterface currentTask;
    private Stack<Integer> futureMoves;

    public ExplorationBrain (TaskContainer taskContainer) {
        this.tasks = taskContainer;
        currentTask = tasks.getTask(TaskType.EXPLORATION);
        futureMoves = new Stack<>();
    }

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds) {
        if (futureMoves.isEmpty()) {
            futureMoves = currentTask.performTask(graph, orientation, pheromoneMarkerDirection);
            return futureMoves.pop();
        } else {
            return futureMoves.pop();
        }
    }
}
