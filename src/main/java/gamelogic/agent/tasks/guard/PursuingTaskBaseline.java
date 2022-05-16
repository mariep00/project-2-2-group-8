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

    private Vector2D placeToGo;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            this.graph = graph;

            Vector2D targetPos = target.position().add(graph.getCurrentPosition().COORDINATES);
            if (!graph.isVisited(targetPos)) {
                try {
                    throw new Exception("The target in PursuingTaskBaseline is not known in the graph!");
                } catch (Exception e) {
                    System.out.println("Current pos " + graph.getCurrentPosition().COORDINATES);
                    System.out.println("Target pos vision " + target.position());
                    System.out.println("Last seen pos of intruder " + intrudersSeen[0].position());
                    System.out.println("Target pos for guard " + targetPos);
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            placeToGo = findGoal(targetPos, target.orientation());
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(placeToGo), 3);
            try {
                placeToGo = nodesToGoal.getFirst();
                this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, false);
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return futureMoves.pop();
    }

    private Vector2D findGoal(Vector2D pos, double orien) {
        Vector2D unitDir;
        if (orien == 0.0) { unitDir = new Vector2D(1, 0);
        } else if (orien == 90.0) { unitDir = new Vector2D(0, 1);
        } else if (orien == 180.0) { unitDir = new Vector2D(-1, 0);
        } else { unitDir = new Vector2D(0, -1);}

        double distanceInFrontOfIntruder = target.position().magnitude()*0.3333334;

        Vector2D goal = pos.add(unitDir.multiply((int) Math.round(distanceInFrontOfIntruder)));
        while (!exists(goal)) {
            goal = goal.add(unitDir.multiply(-1));
        }
        return goal;
    }

    private boolean exists(Vector2D pos) {
        return graph.isVisited(pos);
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
    public boolean isFinished() { return placeToGo.equals(graph.getCurrentPosition().COORDINATES); }

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
