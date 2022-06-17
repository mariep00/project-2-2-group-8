package gamelogic.agent.tasks.guard;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;
import util.MathHelpers;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class FarPursuingTask implements TaskInterface{

    //TODO Adjust the distanceThresholds to make sense
    private final double angleDifference = 30;
    private final double walkingDistanceThreshold = 0.3; // Is multiplied with distance to Intruder
    private final double distanceThresholdFirstPath = 50.0; //Thresholds are multiplied with how long we want to walk
    private final double distanceThresholdSecondPath = 50.5;
    private boolean finished = false;

    private VisionMemory intruder;
    private VisionMemory guard;
    private ExplorationGraph graph;
    private Stack<Integer> futureMoves;

    @Override
    public int performTask (ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            this.graph = graph;
            
            Vector2D currentPosition = graph.getCurrentPosition().COORDINATES;
            double distance = walkingDistanceThreshold * intruder.position().magnitude();
            double angleToGuard = guard.position().angle();
            double angleToIntruder = intruder.position().angle();
            double difference = MathHelpers.differenceBetweenAngles(angleToGuard, angleToIntruder);
            //System.out.println("Angle intruder " + angleToIntruder + ", angle guard " + angleToGuard + ", difference angle " + difference);
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
            return angle == angleToGuard;
        } else if ((angleToIntruder-difference)<0) {
            double angle = angleToIntruder-difference+360.0;
            return angle != angleToGuard;
        } else {
            return angleToGuard > angleToIntruder;
        }
    }

    private LinkedList<Vector2D> findPathToLeft(Vector2D curPos, double distance, double angle) {
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        while (!graph.isVisited(goal)) {
            //System.out.println("0; " + angle);
            angle = checkAngle(angle-10.0);
            goal = VisionController.calculatePoint(curPos, distance, angle);
        }
        while (true) {
            //System.out.println("1; " + angle);
            LinkedList<Vector2D> path1 = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal), (int)(distanceThresholdFirstPath*distance));
            //System.out.println(goal + ", " + graph.getCurrentPosition() + ", " + (int)(distanceThresholdFirstPath*distance) + ", " + Arrays.toString(path1.toArray()));
            if(path1.getFirst().equals(goal)) {
                LinkedList<Vector2D> path2 = AStar.calculate(graph, graph.getNode(goal), graph.getNode(intruder.position().add(graph.getCurrentPosition().COORDINATES)), (int)(distanceThresholdSecondPath*goal.dist(intruder.position().add(graph.getCurrentPosition().COORDINATES))));
                if (path2 == null || path2.getFirst().equals(intruder.position().add(graph.getCurrentPosition().COORDINATES))) { // TODO Added this == null when the intruder's position cannot be reached due to vision and walls
                    return path1;
                }
            }
            angle = checkAngle(angle - 10.0);
            goal = VisionController.calculatePoint(curPos, distance, angle);
            while (!graph.isVisited(goal)) {
                angle = checkAngle(angle-10.0);
                goal = VisionController.calculatePoint(curPos, distance, angle);
                //System.out.println("2; " + angle);
            }
             //System.out.println("3; " + angle);
        }
    }

    private LinkedList<Vector2D> findPathToRight(Vector2D curPos, double distance, double angle) {
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        while (!graph.isVisited(goal)) {
            angle = checkAngle(angle+10.0);
            goal = VisionController.calculatePoint(curPos, distance, angle);
        }
        while (true) {
            //System.out.println("1; " +angle);
            LinkedList<Vector2D> path1 = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal), (int)(distanceThresholdFirstPath*distance));
            if(path1.getFirst().equals(goal)) {
                LinkedList<Vector2D> path2 = AStar.calculate(graph, graph.getNode(goal), graph.getNode(intruder.position().add(graph.getCurrentPosition().COORDINATES)), (int)(distanceThresholdSecondPath*goal.dist(intruder.position())));
                if (path2 == null || path2.getFirst().equals(intruder.position().add(graph.getCurrentPosition().COORDINATES))) {
                    return path1;
                }
            }
            angle = checkAngle(angle + 10.0);
            goal = VisionController.calculatePoint(curPos, distance, angle);
            while (!graph.isVisited(goal)) {
                angle = checkAngle(angle+10.0);
                goal = VisionController.calculatePoint(curPos, distance, angle);
                //System.out.println("temp; " +angle);
            }
            //System.out.println("2; " +angle);
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
    public void setTarget(VisionMemory intruder, VisionMemory guard) {
        this.intruder = intruder;
        this.guard = guard;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public TaskType getType() {
        return TaskType.GUARD_PURSUIT_FAR;
    }

    @Override
    public TaskInterface newInstance() {
        return new FarPursuingTask();
    }
}
