package gamelogic.agent.tasks;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class EvasionTaskBaseline implements TaskInterface{

    private ExplorationGraph graph;
    private TaskType type = TaskType.INTRUDER_EVASION;
    private Stack<Integer> futureMoves;
    private VisionMemory visionTarget;
    private Sound soundTarget;
    private double targetAngle;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves.isEmpty() || futureMoves == null) {
            this.graph = graph;
            futureMoves = new Stack<>();

            double angle = targetAngle - 180.0;
            Vector2D goal = findGoal(angle);
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal));

            this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, 3);
        }
        return futureMoves.pop();
    }

    private double getTargetAngle() {
        if (visionTarget != null) {
            return 360.0 - Math.atan2(visionTarget.position().y, visionTarget.position().x);
        } else {
            return soundTarget.angle();
        }
    }

    private Vector2D findGoal(double angle) {
        if (angle<0) {angle = angle+360.0;
        } else if (angle>360) { angle = angle - 360.0; }
        double distance = 7.0;
        Vector2D curPos = graph.getCurrentPosition().COORDINATES;
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        while (true) {
            if (graph.isVisited(goal)) { break; }
            distance--;
            if (distance<1.0) {
                Vector2D leftGoal = findGoal(angle-5.0);
                Vector2D rightGoal = findGoal(angle+5.0);
                if (curPos.dist(leftGoal) < curPos.dist(rightGoal)) {
                    goal = rightGoal;
                } else { goal = leftGoal; }
            } else { 
                goal = VisionController.calculatePoint(curPos, distance, angle); 
            }
        }
        return goal;
    }

    @Override
    public boolean isFinished() {
        return false; // TODO Add a task finished requirement here
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() {
        return new EvasionTaskBaseline();
    }

    @Override
    public void setTarget(double target) {
        this.targetAngle = target;
    }
}
