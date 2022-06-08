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
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            this.graph = graph;
            target = getUpdatedTarget(intrudersSeen);
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
            LinkedList<Vector2D> path = getPursuitTaskToGoal(placeToGo, distanceInFrontOfIntruder, orientation);

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
        if (futureMoves.size() == 1) finished = true;
        return futureMoves.pop();
    }

    // TODO We should consider the case when guards are approaching an intruder from the same direction, the closest guard should stay in "close pursuit, while the other guard should try to cut off the path of the intruder. Because right now they will just follow each other.
    private LinkedList<Vector2D> getPursuitTaskToGoal(Vector2D goalInFrontOfIntruder, double distanceInFrontOfIntruder, double orientation) {
        boolean firstGoForward = false;
        Vector2D intruderPos = target.position().add(graph.getCurrentPosition().COORDINATES);
        int xDiff = Math.abs(graph.getCurrentPosition().COORDINATES.x-intruderPos.x);
        int yDiff = Math.abs(graph.getCurrentPosition().COORDINATES.y-intruderPos.y);
        if (xDiff <= 3 || yDiff <= 3) firstGoForward = true;

        int[] distanceAdditionsToTry = {0, 1, 2, 3};
        Vector2D firstGoal = null;
        int index = 0;
        while (firstGoal == null || graph.getNode(firstGoal) == null) {
            if (index == distanceAdditionsToTry.length) break;
            if (firstGoForward) {
                firstGoal = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES.getSide(orientation), (target.position().magnitude() - (distanceInFrontOfIntruder+distanceAdditionsToTry[index])), target.position().angle());
            } else firstGoal = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, (target.position().magnitude() - (distanceInFrontOfIntruder+distanceAdditionsToTry[index])), target.position().angle());
            index++;
        }
        if (graph.getNode(firstGoal) == null) return null;
        LinkedList<Vector2D> pathFromFirstToSecondGoal = AStar.calculate(graph, graph.getNode(firstGoal), graph.getNode(goalInFrontOfIntruder));
        if (pathFromFirstToSecondGoal == null) {
            return null;
        }
        LinkedList<Vector2D> pathFromCurrentPosToFirstGoal = null;

        Vector2D positionInFrontOfGuard;
        if (firstGoForward) {
            positionInFrontOfGuard = graph.getCurrentPosition().getCOORDINATES().getSide(orientation);
            if (graph.getNode(positionInFrontOfGuard) != null) {
                pathFromCurrentPosToFirstGoal = AStar.calculate(graph, graph.getNode(positionInFrontOfGuard), graph.getNode(firstGoal));
            }
        }
        if (pathFromCurrentPosToFirstGoal == null) pathFromCurrentPosToFirstGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(firstGoal));
        if (pathFromCurrentPosToFirstGoal == null) return null;
        pathFromFirstToSecondGoal.addAll(pathFromCurrentPosToFirstGoal);
        if (firstGoForward) {
            int tempIndex = pathFromCurrentPosToFirstGoal.size() == 1 ? 0 : pathFromCurrentPosToFirstGoal.size()-2;
            pathFromCurrentPosToFirstGoal.add(tempIndex, graph.getCurrentPosition().getCOORDINATES().getSide(orientation));
        }
        return pathFromFirstToSecondGoal;
    }

    private Vector2D findGoalInFrontOfIntruder(Vector2D pos, double intruderOrientation, double distanceInFrontOfIntruder) {
        Vector2D unitDir = Vector2D.getUnitVectorDirection(intruderOrientation);

        if (unitDir != null) {
            Vector2D goal = pos.add(unitDir.multiply((int) Math.round(distanceInFrontOfIntruder)));
            while (!exists(goal)) {
                goal = goal.add(unitDir.multiply(-1));
            }
            return goal;
        } else return null;
    }

    private boolean exists(Vector2D pos) {
        return graph.isVisited(pos);
    }

    private VisionMemory getUpdatedTarget(VisionMemory[] intrudersSeen) {
        for (VisionMemory visionMemory : intrudersSeen) {
            if (visionMemory != null) {
                double distance = visionMemory.position().dist(target.position());
                if (distance == 1 || distance == 0) return visionMemory;
            }
        }
        return null;
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
    public boolean isFinished() { return finished; }

    @Override
    public Object getTarget() { return target;}

    @Override
    public TaskType getType() {
        return TaskType.GUARD_PURSUIT_CLOSE;
    }

    @Override
    public TaskInterface newInstance() {
        return new ClosePursuingTask();
    }
}
