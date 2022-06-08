package gamelogic.agent.tasks.guard;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

public class FarPursuingTask implements TaskInterface{

    //TODO Adjust the distanceThresholds to make sense
    private final double angleDifference = 30;
    private final double walkingDistanceThreshold = 0.3; // Is multiplied with distance to Intruder
    private final double distanceThresholdFirstPath = 2.0; //Thresholds are multiplied with how long we want to walk
    private final double distanceThresholdSecondPath = 1.5;
    private boolean finished = false;

    private VisionMemory intruder; //TODO: setTarget for intruder and guard
    private VisionMemory guard;
    private ExplorationGraph graph;
    private Stack<Integer> futureMoves;

    @Override
    public int performTask (ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            this.graph = graph;
            
            Vector2D currentPosition = graph.getCurrentPosition().COORDINATES;
            double distance = walkingDistanceThreshold * intruder.position().dist(currentPosition);
            double angleToGuard = guard.position().getAngleBetweenVector(currentPosition);
            double angleToIntruder = intruder.position().getAngleBetweenVector(currentPosition);
            double difference = differenceBetweenAngle(angleToGuard, angleToIntruder);

            boolean toRight = checkDirection(angleToIntruder, angleToGuard, difference);
            if (toRight) {
                double angle = checkAngle(angleToIntruder-angleDifference);
                LinkedList<Vector2D> path = findPathToRight(currentPosition, distance, angle);
                futureMoves = MovementController.convertPath(graph, orientation, path, false);
            } else {
                double angle = checkAngle(angleToIntruder+angleDifference);
                LinkedList<Vector2D> path = findPathToLeft(currentPosition, distance, angle);
                futureMoves = MovementController.convertPath(graph, orientation, path, false);
            }
        }
        if (futureMoves.size() == 1) finished = true;
        return futureMoves.pop();
    }

    private boolean checkDirection(double angleToIntruder, double angleToGuard, double difference) {
        if ((angleToIntruder+difference)>360) {
            double angle = angleToIntruder+difference-360.0;
            if (angle == angleToGuard) {
                return true;
            } else {
                return false;
            }          
        } else if ((angleToIntruder-difference)<0) {
            double angle = angleToIntruder-difference+360.0;
            if (angle == angleToGuard) {
                return false;
            } else {
                return true;
            }
        } else {
            if (angleToGuard>angleToIntruder) {
                return true;
            } else {
                return false;
            }
        }
    }

    private LinkedList<Vector2D> findPathToLeft(Vector2D curPos, double distance, double angle) {
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        while (!graph.isVisited(goal)) {
            angle = checkAngle(angle-10.0);
            goal = VisionController.calculatePoint(curPos, distance, angle);
        }
        while (true) {
            LinkedList<Vector2D> path1 = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal), (int)(distanceThresholdFirstPath*distance));
            if(path1.getFirst().equals(goal)) {
                LinkedList<Vector2D> path2 = AStar.calculate(graph, graph.getNode(goal), graph.getNode(intruder.position()), (int)(distanceThresholdSecondPath*goal.dist(intruder.position())));
                if (path2.getFirst().equals(intruder.position())) {
                    return path1;
                }
            }
            angle = angle - 10.0;
            goal = VisionController.calculatePoint(curPos, distance, angle);
            while (!graph.isVisited(goal)) {
                angle = checkAngle(angle-10.0);
                goal = VisionController.calculatePoint(curPos, distance, angle);
            }
        }
    }

    private LinkedList<Vector2D> findPathToRight(Vector2D curPos, double distance, double angle) {
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        while (!graph.isVisited(goal)) {
            angle = checkAngle(angle+10.0);
            goal = VisionController.calculatePoint(curPos, distance, angle);
        }
        while (true) {
            LinkedList<Vector2D> path1 = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal), (int)(distanceThresholdFirstPath*distance));
            if(path1.getFirst().equals(goal)) {
                LinkedList<Vector2D> path2 = AStar.calculate(graph, graph.getNode(goal), graph.getNode(intruder.position()), (int)(distanceThresholdSecondPath*goal.dist(intruder.position())));
                if (path2.getFirst().equals(intruder.position())) {
                    return path1;
                }
            }
            angle = angle + 10.0;
            goal = VisionController.calculatePoint(curPos, distance, angle);
            while (!graph.isVisited(goal)) {
                angle = checkAngle(angle+10.0);
                goal = VisionController.calculatePoint(curPos, distance, angle);
            }
        }
    }

    private double differenceBetweenAngle(double a, double b) {
        double difference = Math.abs(a-b);
        if (difference>180) {
            return difference-180;
        } else {
            return difference;
        }
    }

    private double checkAngle(double angle) {
        if (angle > 360) {
            return angle-360.0;
        } else if ( angle < 0) {
            return angle+360.0;
        } else {
            return angle;
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public TaskType getType() {
        return TaskType.GUARD_PURSUIT;
    }

    @Override
    public TaskInterface newInstance() {
        return new FarPursuingTask();
    }
}
