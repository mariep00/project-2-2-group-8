package gamelogic.agent.brains;

import gamelogic.agent.tasks.ExplorationTaskFrontier;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.Sound;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;
import java.util.Stack;

public class ExplorationBrain implements BrainInterface {
    private TaskContainer tasks;
    private Stack<Integer> futureMoves;

    public ExplorationBrain () {
        tasks = new TaskContainer();
        tasks.addTask(new ExplorationTaskFrontier());
        tasks.switchToTask(TaskType.EXPLORATION);
        futureMoves = new Stack<>();
    }

    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds) {
        if (futureMoves.isEmpty()) {
            futureMoves = tasks.getCurrentTask().performTask(graph, orientation, pheromoneMarkerDirection);
            return futureMoves.pop();
        } else {
            return futureMoves.pop();
        }
    }
}
