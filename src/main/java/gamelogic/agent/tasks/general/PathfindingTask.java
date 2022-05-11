package gamelogic.agent.tasks.general;

import java.util.LinkedList;
import java.util.Stack;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.MovementController;
import gamelogic.maps.graph.ExplorationGraph;

public class PathfindingTask implements TaskInterface {

    private TaskType type = TaskType.PATHFINDING;
    private Vector2D target;
    private Stack<Integer> futureMoves;
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection){
        if (futureMoves.isEmpty() || futureMoves == null) {
            futureMoves = new Stack<>();
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(target));
       
            futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, -1);
        }
        if(futureMoves.size()==1) finished=true;
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

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((PathfindingTask) other).getTarget().equals(this.target);
        }
        return false;
    }
}
