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
                //System.out.println("----- Pheromone Angle: " + pheromoneMarkerDirection);
                sortObjects = new SortObject[nodes.size()];
                for (int i = 0; i < nodes.size(); i++) {
                    sortObjects[i] = new SortObject<>(nodes.get(i), nodes.get(i).COORDINATES.dist(targetCenter));
                }

                sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
                
                ArrayList<Node> candidates = new ArrayList<>();
                double smallestDistance = sortedArray[0].sortParameter;
                double largestDistance = sortedArray[sortedArray.length-1].sortParameter;
                double distanceThreshold = smallestDistance + (Math.abs(largestDistance-smallestDistance)/1.5);
                //System.out.println("----- Threshold: " + distanceThreshold);
                for (int i=0; i<sortedArray.length; i++) {
                    SortObject<Node> sortObject = sortedArray[i];
                    if (sortObject.sortParameter<=distanceThreshold) {
                        candidates.add(sortObject.object);
                    }
                }
                //System.out.println("----- Candidate Size: " +candidates.size() );
                if (sortedArrayPheromoneAngle == null) {
                    double[] frontierAnglesDiffPheromone = new double[candidates.size()];
                    for (int i = 0; i < frontierAnglesDiffPheromone.length; i++) {
                        frontierAnglesDiffPheromone[i] = Math.abs(graph.getCurrentPosition().COORDINATES.getAngleBetweenVector(candidates.get(i).COORDINATES) - checkAngle(pheromoneMarkerDirection - 180));
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
        /*System.out.println("----- Chose Frontier at: " + sortedArrayPheromoneAngle[index].object);
        System.out.println("----- With Target center at: "+ targetCenter);
        System.out.println("----- Angle to Frontier: " + graph.getCurrentPosition().COORDINATES.getAngleBetweenVector(sortedArrayPheromoneAngle[index].object.COORDINATES));
        System.out.println("----- Angle difference: " + sortedArrayPheromoneAngle[index].sortParameter);*/
        return sortedArrayPheromoneAngle[index].object;
    }

    @Override
    public boolean isFinished() { return graph.getCurrentPosition().COORDINATES.dist(targetCenter) < 4; }
    
    @Override
    public TaskType getType() { return type; }

    @Override
    public TaskInterface newInstance() { return new ExplorationInDirection(); }

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

    private double checkAngle(double angle) {
        if (angle > 360) {
            return angle-360.0;
        } else if ( angle < 0) {
            return angle+360.0;
        } else {
            return angle;
        }
    }
}
