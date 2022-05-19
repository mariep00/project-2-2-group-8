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
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (task == null || graph.frontiers.isEmpty()) {
            double maxDistance = Controller.addNoise(50.0 * (1-guardYellToFind.loudness()), (1-guardYellToFind.loudness())*10);
            double minDistance = Controller.addNoise((50.0 / 2.0) * (1-guardYellToFind.loudness()), (1-guardYellToFind.loudness())*10);
            Vector2D startingPosition = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, maxDistance, guardYellToFind.angle());
            Vector2D possiblePosition = getPossibleOriginGuardYell(graph, startingPosition, maxDistance, minDistance, guardYellToFind);
            // TODO Add smth that checks if the path length is in between our max and min distance, otherwise the target pos doesn't make sense
            if (possiblePosition != null) {
                LinkedList<Vector2D> path = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(possiblePosition));
                if (path != null) {
                    task = new PathfindingTask();
                    task.setTarget(graph, orientation, path);
                    task.setTarget(possiblePosition);
                }
            }
            if (task == null) {
                if (graph.frontiers.isEmpty()) {
                    try {
                        throw new Exception("Agent wants to do exploration in direction, but it doesn't have any frontiers :(");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                // No position was found in direction, so do explorationInDirection
                task = new ExplorationInDirection();
                Vector2D potentialGoal = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, maxDistance, guardYellToFind.angle());
                task.setTarget(potentialGoal);
            }
        }
        int taskNr = task.performTask(graph, orientation, pheromoneMarkerDirection, sounds, guardsSeen, intrudersSeen);
        finished = task.isFinished();
        return taskNr;
    }

    private Vector2D getPossibleOriginGuardYell(ExplorationGraph graph, Vector2D startingPosition, double maxDistance, double minDistance, Sound guardYellToFind) {
        double currentDistance = graph.getCurrentPosition().COORDINATES.dist(startingPosition);
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
    public boolean isFinished() { return finished; }

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
