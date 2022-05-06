package gamelogic.agent.tasks;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.MovementController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

public class PursuingTaskBaseline implements TaskInterface {

    private ExplorationGraph graph;
    private TaskType type = TaskType.PURSUIT;

    @Override
    public Stack<Integer> performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        this.graph = graph;
        VisionMemory closestIntruder = getActiveIntruder(intrudersSeen);
       
        Vector2D placeToGo = findGoal(closestIntruder.position(), closestIntruder.orientation());
        LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(placeToGo));
        
        return MovementController.convertPath(graph, orientation, nodesToGoal, 3);
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

    private VisionMemory getActiveIntruder(VisionMemory[] intrudersSeen) {
        VisionMemory closestIntruder = new VisionMemory(null, Double.MAX_VALUE, 0.0);
        for (int i=0; i<intrudersSeen.length; i++) {
            if (intrudersSeen[i].secondsAgo() < closestIntruder.secondsAgo()) {
                closestIntruder = intrudersSeen[i];
            }
        }
        return closestIntruder;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() {
        return new PursuingTaskBaseline();
    }

    @Override
    public Stack<Integer> performTask() throws UnsupportedOperationException{
        return null;
    }

    @Override
    public Stack<Integer> performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection) throws UnsupportedOperationException{
        return null;
    }
}
