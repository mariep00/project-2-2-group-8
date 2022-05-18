package gamelogic.agent.tasks.guard;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.general.ExplorationInDirection;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ClosePursuingTask implements TaskInterface {

    private ExplorationGraph graph;
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

            double distanceInFrontOfIntruder = target.position().magnitude()*0.3333334;
            placeToGo = findGoalInFrontOfIntruder(targetPos, target.orientation(), distanceInFrontOfIntruder);
            LinkedList<Vector2D> path = getPursuitTaskToGoal(placeToGo, distanceInFrontOfIntruder);

            if (path != null) {
                placeToGo = path.getFirst();
                this.futureMoves = MovementController.convertPath(graph, orientation, path, false);
            }
            else {
                ExplorationInDirection tempTask = new ExplorationInDirection();
                tempTask.setTarget(placeToGo);
                return tempTask.performTask(graph, orientation, pheromoneMarkerDirection, sounds, guardsSeen, intrudersSeen);
            }
        }
        return futureMoves.pop();
    }

    // TODO We should consider the case when guards are approaching an intruder from the same direction, the closest guard should stay in "close pursuit, while the other guard should try to cut off the path of the intruder. Because right now they will just follow each other.
    // TODO Add step in between, so when the intruder keeps turning left and right w.r.t. the guard, the guard doesn't and just walks forward
    private LinkedList<Vector2D> getPursuitTaskToGoal(Vector2D goalInFrontOfIntruder, double distanceInFrontOfIntruder) {
        Vector2D firstGoal = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, (target.position().magnitude()-distanceInFrontOfIntruder), target.position().angle());
        if (graph.getNode(firstGoal) == null) return null;
        LinkedList<Vector2D> pathFromFirstToSecondGoal = AStar.calculate(graph, graph.getNode(firstGoal), graph.getNode(goalInFrontOfIntruder));
        LinkedList<Vector2D> pathFromCurrentPosToFirstGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(firstGoal));
        pathFromFirstToSecondGoal.addAll(pathFromCurrentPosToFirstGoal);
        return pathFromFirstToSecondGoal;
    }

    private Vector2D findGoalInFrontOfIntruder(Vector2D pos, double intruderOrientation, double distanceInFrontOfIntruder) {
        Vector2D unitDir;
        if (intruderOrientation == 0.0) { unitDir = new Vector2D(1, 0);
        } else if (intruderOrientation == 90.0) { unitDir = new Vector2D(0, 1);
        } else if (intruderOrientation == 180.0) { unitDir = new Vector2D(-1, 0);
        } else { unitDir = new Vector2D(0, -1);}

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
            return ((ClosePursuingTask) other).getTarget().equals(this.target);
        }
        return false;
    }

    @Override
    public boolean isFinished() { return placeToGo.equals(graph.getCurrentPosition().COORDINATES); }

    @Override
    public Object getTarget() { return target;}

    @Override
    public TaskType getType() {
        return TaskType.GUARD_PURSUIT;
    }

    @Override
    public TaskInterface newInstance() {
        return new ClosePursuingTask();
    }
}