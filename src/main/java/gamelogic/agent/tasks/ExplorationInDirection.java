package gamelogic.agent.tasks;

import java.util.ArrayList;
import java.util.LinkedList;

import datastructures.Vector2D;
import datastructures.quicksort.QuickSort;
import datastructures.quicksort.SortObject;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

@SuppressWarnings("unchecked")

public class ExplorationInDirection extends ExplorationTaskFrontier{
    
    private ExplorationGraph graph;
    private TaskType type = TaskType.EXPLORATION_DIRECTION;
    private Vector2D target;
    private int markerThreshold = 0;
    private int markerIndex = 0;
    private SortObject<Node>[] sortedArray;
    private SortObject<Node>[] sortedArrayPheromoneAngle;

    @Override
    protected Node getNextFrontier(int index, double pheromoneMarkerDirection) {
        LinkedList<Node> nodes = graph.frontiers.getAllNodes();
        if (nodes.isEmpty()) return null;
        SortObject<Node>[] sortObjects = new SortObject[nodes.size()];

        QuickSort<Node> quickSort = new QuickSort<>();

        // Only sort when starting with a new selection of nodes
        if (index == 0) {
            if (pheromoneMarkerDirection != -1) {
                if (sortedArrayPheromoneAngle == null) {
                    double[] frontierAnglesDiffPheromone = new double[nodes.size()];
                    for (int i = 0; i < frontierAnglesDiffPheromone.length; i++) {
                        frontierAnglesDiffPheromone[i] = Math.abs(180 - Math.abs(pheromoneMarkerDirection - graph.getCurrentPosition().COORDINATES.getAngleBetweenVector(nodes.get(i).COORDINATES)));
                    }

                    for (int i = 0; i < nodes.size(); i++) {
                        sortObjects[i] = new SortObject<>(nodes.get(i), frontierAnglesDiffPheromone[i]);
                    }

                    sortedArrayPheromoneAngle = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
                }
                ArrayList<Node> candidates = new ArrayList<>();
                double threshold = 30 + (markerThreshold * 5);
                while (candidates.size() == 0) {
                    for (int i = markerIndex; i < sortedArrayPheromoneAngle.length; i++) {
                        SortObject<Node> sortObject = sortedArrayPheromoneAngle[i];
                        if (sortObject.sortParameter <= threshold) candidates.add(sortObject.object);
                        else {
                            markerIndex = i;
                            break;
                        }
                    }
                    threshold += 5;
                    markerThreshold++;
                }

                sortObjects = new SortObject[candidates.size()];
                for (int i = 0; i < candidates.size(); i++) {
                    sortObjects[i] = new SortObject<>(candidates.get(i), candidates.get(i).COORDINATES.dist(target));
                }

                sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
            } else {
                for (int i = 0; i < nodes.size(); i++) {
                    sortObjects[i] = new SortObject<>(nodes.get(i), nodes.get(i).COORDINATES.dist(target));
                }
                sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
            }
        }

        return sortedArray[index].object;
    }
    @Override
    public TaskType getType() {
        return type;
    }
    @Override
    public TaskInterface newInstance() {
        return new ExplorationInDirection();
    }
    @Override
    public void setTarget(Vector2D target) {
        this.target = target;
    }
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((ExplorationInDirection) other).getTarget().equals(this.target);
        }
        return false;
    }
    @Override
    public Object getTarget() {
        return target;
    }
}