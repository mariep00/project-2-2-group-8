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
    private boolean isFinished = false;

    // TODO I know this task can get stuck, should be fixed (at least partially :)) by adding a new task to perform to the decider
    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (target == null || target.equals(graph.getCurrentPosition().COORDINATES)) {
            currentIndex++;
            if (currentIndex >= intrudersSeen.length) currentIndex = 0;
            while (intrudersSeen[currentIndex] == null) {
                currentIndex++;
                if (currentIndex >= intrudersSeen.length) currentIndex = 0;
            }

            if (graph.getCurrentPosition().COORDINATES.equals(intrudersSeen[currentIndex].position())) isFinished = true;
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
    public boolean isFinished() { return isFinished; }

    @Override
    public TaskInterface newInstance() {
        return new VisitLastSeenIntruderPositions();
    }
}
