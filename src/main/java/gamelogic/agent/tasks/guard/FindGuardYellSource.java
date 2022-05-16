package gamelogic.agent.tasks.guard;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.general.ExplorationInDirection;
import gamelogic.agent.tasks.general.PathfindingTask;
import gamelogic.controller.Controller;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;

public class FindGuardYellSource implements TaskInterface {

    private Sound guardYellToFind;
    private TaskInterface task;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (task == null) {
            double maxDistance = Controller.addNoise(50 * guardYellToFind.loudness(), 8);
            double minDistance = Controller.addNoise(((float) 50 / 2) * guardYellToFind.loudness(), 8);
            Vector2D startingPosition = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, maxDistance, guardYellToFind.angle());
            Vector2D possiblePosition = getPossibleOriginGuardYell(graph, startingPosition, maxDistance, minDistance, guardYellToFind);
            if (possiblePosition != null) {
                while (true) {
                    LinkedList<Vector2D> path = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(possiblePosition));
                    if (path.size() >= minDistance && path.size() <= maxDistance) {
                        task = new PathfindingTask();
                        task.setTarget(graph, orientation, path);

                    } else {
                        possiblePosition = getPossibleOriginGuardYell(graph, possiblePosition, maxDistance, minDistance, guardYellToFind);
                        if (possiblePosition == null) break;
                    }
                }
            }
            // No position was found in direction, so do explorationInDirection
            task = new ExplorationInDirection();
            Vector2D potentialGoal = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, maxDistance / 2, guardYellToFind.angle());
            task.setTarget(potentialGoal);
        }
        return task.performTask(graph, orientation, pheromoneMarkerDirection, sounds, guardsSeen, intrudersSeen);
    }

    private Vector2D getPossibleOriginGuardYell(ExplorationGraph graph, Vector2D startingPosition, double maxDistance, double minDistance, Sound guardYellToFind) {
        double currentDistance = maxDistance;
        Vector2D currentPosition = startingPosition;
        while (!graph.isVisited(currentPosition) || graph.getNode(currentPosition).getTile().isWall()) {
            if (currentDistance < minDistance) {
                return null;
            }
            currentDistance--;
            currentPosition = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, currentDistance, guardYellToFind.angle());
        }
        return currentPosition;
    }

    @Override
    public void setTarget(Sound guardYellToFind) { this.guardYellToFind = guardYellToFind; }

    @Override
    public TaskContainer.TaskType getType() {
        return TaskContainer.TaskType.FIND_GUARD_YELL_SOURCE;
    }

    @Override
    public TaskInterface newInstance() {
        return new FindGuardYellSource();
    }
}
