package gamelogic.agent.brains;

import java.util.Stack;

import gamelogic.agent.tasks.ExplorationTaskFrontier;
import gamelogic.agent.tasks.ExplorationTaskRandom;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.maps.graph.ExplorationGraph;

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
    public int makeDecision(ExplorationGraph graph, double orientation) {
        if (futureMoves.isEmpty()) {
            futureMoves = tasks.getCurrentTask().performTask(graph, orientation);
            return futureMoves.pop();
        } else {
            return futureMoves.pop();
        }
    }
}
