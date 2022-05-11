package gamelogic.agent.tasks.guard;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.Controller;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.Stack;

public class FindGuardYellSource implements TaskInterface {
    private Stack<Integer> futureMoves;
    private Sound guardYellToFind;
    private ExplorationGraph graph;

    @Override
    public int performTask(ExplorationGraph graph, double orientation) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            this.graph = graph;
            double maxDistance = Controller.addNoise(50*guardYellToFind.loudness(), 8);
            double minDistance = Controller.addNoise(((float)50/2)*guardYellToFind.loudness(), 8);
            Vector2D startingPosition = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, maxDistance, guardYellToFind.angle());
            Vector2D possiblePosition = getPossiblePosition(startingPosition, maxDistance, minDistance);
            if (possiblePosition != null) {
                while (true) {
                    LinkedList<Vector2D> path = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(startingPosition));
                    if (path.size() >= minDistance && path.size() <= maxDistance) {
                        this.futureMoves = MovementController.convertPath(graph, orientation, path, -1);
                        return futureMoves.pop();
                    } else {
                        possiblePosition = getPossiblePosition(possiblePosition, maxDistance, minDistance);
                        if (possiblePosition == null) break;
                    }
                }
            }
            // No position was found in direction, so do explorationInDirection

        }
        return futureMoves.pop();
    }

    private Vector2D getPossiblePosition(Vector2D startingPosition, double maxDistance, double minDistance) {
        double currentDistance = maxDistance;
        Vector2D currentPosition = startingPosition;
        while (!graph.isVisited(currentPosition)) {
            if (currentDistance < minDistance) {
                return null;
            }
            currentDistance--;
            currentPosition = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, currentDistance, guardYellToFind.angle());
        }
        return currentPosition;
    }

    @Override
    public void setTarget(Sound target) { this.guardYellToFind = target; }

    @Override
    public TaskContainer.TaskType getType() {
        return TaskContainer.TaskType.FIND_GUARD_YELL_SOURCE;
    }

    @Override
    public TaskInterface newInstance() {
        return new FindGuardYellSource();
    }
}
