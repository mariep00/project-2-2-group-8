package gamelogic.agent.tasks.general;

import datastructures.Vector2D;
import datastructures.quicksort.QuickSort;
import datastructures.quicksort.SortObject;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.maps.graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

@SuppressWarnings("unchecked")

public class ExplorationInDirection extends ExplorationTaskFrontier{

    private TaskType type = TaskType.EXPLORATION_DIRECTION;
    private Vector2D targetCenter;

    @Override
    protected Node getNextFrontier(int index, double pheromoneMarkerDirection) {
        LinkedList<Node> nodes = graph.frontiers.getAllNodes();
        if (nodes.isEmpty()) return null;
        SortObject<Node>[] sortObjects = new SortObject[nodes.size()];

        QuickSort<Node> quickSort = new QuickSort<>();

        // Only sort when starting with a new selection of nodes
        if (index == 0) {
            if (pheromoneMarkerDirection != -1) {
                sortObjects = new SortObject[nodes.size()];
                for (int i = 0; i < nodes.size(); i++) {
                    sortObjects[i] = new SortObject<>(nodes.get(i), nodes.get(i).COORDINATES.dist(targetCenter));
                }

                sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
                
                ArrayList<Node> candidates = new ArrayList<>();
                double smallestDistance = sortedArray[0].sortParameter;
                double largestDistance = sortedArray[sortedArray.length-1].sortParameter;
                double distanceThreshold = largestDistance-(Math.abs(largestDistance-smallestDistance)/2.0);
                for (int i=0; i<sortedArray.length; i++) {
                    SortObject<Node> sortObject = sortedArray[i];
                    if (sortObject.sortParameter<=distanceThreshold) {
                        candidates.add(sortObject.object);
                    }
                }
                if (sortedArrayPheromoneAngle == null) {
                    double[] frontierAnglesDiffPheromone = new double[candidates.size()];
                    for (int i = 0; i < frontierAnglesDiffPheromone.length; i++) {
                        frontierAnglesDiffPheromone[i] = Math.abs(180 - Math.abs(pheromoneMarkerDirection - candidates.get(i).COORDINATES.getAngleBetweenVector(graph.getCurrentPosition().COORDINATES)));
                    }

                    for (int i = 0; i < candidates.size(); i++) {
                        sortObjects[i] = new SortObject<>(candidates.get(i), frontierAnglesDiffPheromone[i]);
                    }

                    sortedArrayPheromoneAngle = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
                }
            } else {
                for (int i = 0; i < nodes.size(); i++) {
                    sortObjects[i] = new SortObject<>(nodes.get(i), nodes.get(i).COORDINATES.dist(targetCenter));
                }
                // Name is weird, don't think about it :)
                sortedArrayPheromoneAngle = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
            }
        }
        //System.out.println("Corresponding sorted array " + Arrays.toString(sortedArrayPheromoneAngle));
        //System.out.println("        Goal Frontier: " + sortedArrayPheromoneAngle[index].object);
        return sortedArrayPheromoneAngle[index].object;
    }
    @Override
    public boolean isFinished() { return graph.getCurrentPosition().COORDINATES.dist(targetCenter) <= 5; }
    @Override
    public TaskType getType() {
        return type;
    }
    @Override
    public TaskInterface newInstance() {
        return new ExplorationInDirection();
    }
    @Override
    public void setTarget(Vector2D target) { this.targetCenter = target; }
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((ExplorationInDirection) other).getTarget().equals(this.targetCenter);
        }
        return false;
    }
    @Override
    public Object getTarget() {
        return targetCenter;
    }
}
