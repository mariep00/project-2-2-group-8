package gamelogic.agent.tasks;

import java.util.List;
import java.util.Stack;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

public class AvoidCollisionTask implements TaskInterface{

    private TaskType type = TaskType.AVOID_COLLISION;
    private Stack<Integer> futureMoves;
    private Vector2D target;
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves.isEmpty() || futureMoves == null) {
            futureMoves = new Stack<>();
            futureMoves.push(1);
            futureMoves.push(0);
        }
        if (futureMoves.size()==1) finished=true;
        return futureMoves.pop();
    } 

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() {
        return new AvoidCollisionTask();
    }

    @Override
    public void setTarget(Vector2D target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((AvoidCollisionTask) other).getTarget().equals(this.target);
        }
        return false;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
    
}
