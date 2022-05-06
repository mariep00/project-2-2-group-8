package gamelogic.agent.tasks;

import java.util.Stack;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.maps.graph.ExplorationGraph;

public class EvasionTaskBaseline implements TaskInterface{

    @Override
    public Stack<Integer> performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection) {
        Vector2D guardPos = new Vector2D(0, 0);
        double guardOrientation = 0.0;

        double angle = 360.0-Math.atan2(guardPos.y, guardPos.x);
        angle = angle-180.0;
        if (angle<0) {angle = angle+360.0;
        } else if (angle>360) { angle = angle - 360.0; }
        Vector2D goal = findGoal(angle);
        return null;
    }

    private Vector2D findGoal(double angle) {

        
        return null;
    }

    @Override
    public TaskType getType() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
