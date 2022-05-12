package gamelogic.agent.tasks.guard;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.MovementController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class VisitLastSeenIntruderPositions implements TaskInterface {
    private Stack<Integer> futureMoves;
    private int currentIndex = 0;
    private Vector2D target;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        // TODO Only an issue when game starts with only 1 intruder. Otherwise, vision won't be removed when intruder is caught, while guard doesn't know an intruder was caught. If only 1 intruder is left, and the last position it was seen is reached the guard will get stuck if no other input is being detected.
        if (target == null || target.equals(graph.getCurrentPosition().COORDINATES)) {
            currentIndex++;
            if (currentIndex >= intrudersSeen.length) currentIndex = 0;
            while (intrudersSeen[currentIndex] == null) {
                currentIndex++;
                if (currentIndex >= intrudersSeen.length) currentIndex = 0;
            }
        }
        if (futureMoves == null || futureMoves.isEmpty()) {
            target = intrudersSeen[currentIndex].position().add(graph.getCurrentPosition().COORDINATES);
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(target));

            this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, true);
        }
        return futureMoves.pop();
    }

    @Override
    public TaskContainer.TaskType getType() {
        return TaskContainer.TaskType.VISIT_LAST_SEEN_INTRUDER_POSITIONS;
    }

    @Override
    public TaskInterface newInstance() {
        return new VisitLastSeenIntruderPositions();
    }
}
