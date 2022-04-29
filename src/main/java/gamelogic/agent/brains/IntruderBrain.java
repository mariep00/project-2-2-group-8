package gamelogic.agent.brains;

import gamelogic.agent.tasks.ExplorationTaskFrontier;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.datacarriers.Sound;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;

public class IntruderBrain implements BrainInterface {

    private TaskContainer tasks;

    public IntruderBrain () {
        tasks = new TaskContainer();
        tasks.addTask(new ExplorationTaskFrontier());
        tasks.switchToTask(TaskType.EXPLORATION);
        //TODO: implement other tasks than exploration for intruders and add them to the container
    }

    //TODO: Add logic for when to switch between different tasks in the container
    @Override
    public int makeDecision(ExplorationGraph graph, double orientation, double pheromoneMarker, List<Sound> sounds) {
        return -1;
    }
}
