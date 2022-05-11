package gamelogic.agent.tasks.guard;

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

public class PursuingTaskBaseline implements TaskInterface {

    private ExplorationGraph graph;
    private TaskType type = TaskType.GUARD_PURSUIT;
    private Stack<Integer> futureMoves;
    private VisionMemory target;
    private Vector2D targetPos;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves.isEmpty()) {
            this.graph = graph;
            //VisionMemory closestIntruder = getActiveIntruder(intrudersSeen);

            targetPos = target.position().add(graph.getCurrentPosition().COORDINATES);
            Vector2D placeToGo = findGoal(targetPos, target.orientation());
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(placeToGo));

            this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, 3);
        }
        return futureMoves.pop();
    }

    private Vector2D findGoal(Vector2D pos, double orien) {
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
    public void setTarget(VisionMemory target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((PursuingTaskBaseline) other).getTarget().equals(this.target);
        }
        return false;
    }

    @Override
    public boolean isFinished() { return targetPos.equals(graph.getCurrentPosition().COORDINATES); }

    @Override
    public Object getTarget() { return target;}

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() {
        return new PursuingTaskBaseline();
    }
}
