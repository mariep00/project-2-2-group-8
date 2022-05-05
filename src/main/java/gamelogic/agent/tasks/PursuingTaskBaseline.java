package gamelogic.agent.tasks;

import java.util.LinkedList;
import java.util.Stack;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.MovementController;
import gamelogic.maps.graph.ExplorationGraph;

public class PursuingTaskBaseline implements TaskInterface {

    ExplorationGraph graph;
    private TaskType type = TaskType.PURSUIT;

    @Override
    public Stack<Integer> performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection) {
        this.graph = graph;
        // TODO: Needs to be passed to this method:
        Vector2D intruderInVision = new Vector2D(0, 0);
        double orientationOfIntruder = 0.0;
       
        Vector2D placeToGo = findGoal(intruderInVision, orientationOfIntruder);
        LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(placeToGo));
        
        return MovementController.convertPath(graph, orientation, nodesToGoal, 3);
    }

    @Override
    public TaskType getType() {
        return type;
    }

    private Vector2D findGoal (Vector2D pos, double orien) {
        Vector2D unitDir;
        if (orien == 0.0) { unitDir = new Vector2D(1, 0);
        } else if (orien == 90.0) { unitDir = new Vector2D(0, 1);
        } else if (orien == 180.0) { unitDir = new Vector2D(-1, 0);
        } else { unitDir = new Vector2D(0, -1);}
        Vector2D goal = pos.add(unitDir.multiply(3));

        while (true) {
            if (exists(goal)) { break; }
            goal = goal.add(unitDir.multiply(-1));
        }
        return goal;
    }

    private boolean exists(Vector2D pos) {
        return graph.isVisited(pos);
    }
}
