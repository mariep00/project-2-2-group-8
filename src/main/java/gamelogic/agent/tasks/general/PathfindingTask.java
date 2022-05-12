package gamelogic.agent.tasks.general;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.MovementController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class PathfindingTask implements TaskInterface {

    private TaskType type = TaskType.PATHFINDING;
    private Vector2D target;
    private Stack<Integer> futureMoves;
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            futureMoves = new Stack<>();
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(target));
       
            futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, false);
        }
        if(futureMoves.size()==1) finished=true;
        return futureMoves.pop();
    }

    public void setPath(ExplorationGraph graph, double orientation, LinkedList<Vector2D> path) {
        this.futureMoves = MovementController.convertPath(graph, orientation, path, false);
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
