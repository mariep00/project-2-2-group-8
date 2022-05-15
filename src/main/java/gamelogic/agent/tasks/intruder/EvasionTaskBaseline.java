package gamelogic.agent.tasks.intruder;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class EvasionTaskBaseline implements TaskInterface {

    private ExplorationGraph graph;
    private TaskType type = TaskType.INTRUDER_EVASION;
    private Stack<Integer> futureMoves;
    private double targetAngle;
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            this.graph = graph;
            futureMoves = new Stack<>();

            double angle = targetAngle - 180.0;
            Vector2D goal = findGoal(angle);
            //System.out.println("new Task created");
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal), 3);

            this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, false);
        }
        if (futureMoves.size()==1) finished=true;
        return futureMoves.pop();
    }

    private Vector2D findGoal(double angle) {
        //System.out.println("Initial Angle " + angle);
        if (angle<0) {angle = angle+360.0;
        } else if (angle>360) { angle = angle - 360.0; }
        double distance = 10.0;
        Vector2D curPos = graph.getCurrentPosition().COORDINATES;
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        //System.out.println("Current Pos: " + curPos + " Goal: " + goal);
        //System.out.println("Number of nodes: " + graph.getNumberOfNodes());
        int counter = 0;
        while (true) {
            if (graph.isVisited(goal)) { return goal; }
            if (counter > 10) return curPos;
            distance--;
            counter++;
            if (distance<1.0) {
                angle = angle+10.0;
                //System.out.println("angle = " + angle);
                distance=10.0;
                goal = VisionController.calculatePoint(curPos, distance, angle); 
                //Vector2D leftGoal = findGoalLeft(angle-5.0);
                //Vector2D rightGoal = findGoalRight(angle+5.0);
                //System.out.println("Left Goal Angle " + (angle-5.0));
                //System.out.println("Right Goal Angle " + (angle+5.0));
                /*if (curPos.dist(leftGoal) < curPos.dist(rightGoal)) {
                    goal = rightGoal;
                } else { goal = leftGoal; }*/
            } else { 
                goal = VisionController.calculatePoint(curPos, distance, angle); 
            }
        }
    }

    private Vector2D findGoalLeft(double angle) {
        if (angle<0) {angle = angle+360.0;
        } else if (angle>360) { angle = angle - 360.0; }
        double distance = 10.0;
        Vector2D curPos = graph.getCurrentPosition().COORDINATES;
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        while (true) {
            if (graph.isVisited(goal)) { return goal; }
            distance--;
            if (distance<4.0) {
                Vector2D leftGoal = findGoalLeft(angle-5.0);
                goal = leftGoal;
            } else { 
                goal = VisionController.calculatePoint(curPos, distance, angle); 
            }
        }
    }

    private Vector2D findGoalRight(double angle) {
        if (angle<0) {angle = angle+360.0;
        } else if (angle>360) { angle = angle - 360.0; }
        double distance = 10.0;
        Vector2D curPos = graph.getCurrentPosition().COORDINATES;
        Vector2D goal = VisionController.calculatePoint(curPos, distance, angle);
        while (true) {
            if (graph.isVisited(goal)) { return goal; }
            distance--;
            if (distance<4.0) {
                Vector2D leftGoal = findGoalRight(angle+5.0);
                goal = leftGoal;
            } else { 
                goal = VisionController.calculatePoint(curPos, distance, angle); 
            }
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
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
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((EvasionTaskBaseline) other).getTarget().equals(this.targetAngle);
        }
        return false;
    }
}
