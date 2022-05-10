package gamelogic.agent.tasks;

import java.util.LinkedList;
import java.util.Stack;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.MovementController;
import gamelogic.maps.graph.ExplorationGraph;

public class PathfindingTask implements TaskInterface {

    private TaskType type = TaskType.PATHFINDING;
    private Vector2D target;
    private Stack<Integer> futureMoves;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection){
        if (futureMoves.isEmpty() || futureMoves == null) {
            futureMoves = new Stack<>();
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(target));
       
            futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, -1);
        }
        return futureMoves.pop();
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() {
        return new PathfindingTask();
    }
    
    @Override
    public void setTarget(Vector2D target) {
        this.target = target;
    }
}
