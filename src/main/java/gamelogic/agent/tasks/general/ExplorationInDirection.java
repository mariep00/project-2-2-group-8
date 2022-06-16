package gamelogic.agent.tasks.general;

import datastructures.Vector2D;
import datastructures.quicksort.QuickSort;
import datastructures.quicksort.SortObject;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.maps.graph.Node;

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
                double[] diffNodeAndPheromone = new double[nodes.size()];
                double[] distances = new double[nodes.size()];
                for (int i = 0; i < nodes.size(); i++) {
                    diffNodeAndPheromone[i] = Math.abs(graph.getCurrentPosition().COORDINATES.getAngleBetweenVector(nodes.get(i).COORDINATES) - checkAngle(pheromoneMarkerDirection - 180));
                    distances[i] = nodes.get(i).COORDINATES.dist(targetCenter);
                }

                diffNodeAndPheromone = normalizeBetweenZeroAndTen(diffNodeAndPheromone);
                distances = normalizeBetweenZeroAndTen(distances);

                sortObjects = new SortObject[nodes.size()];
                for (int i = 0; i < nodes.size(); i++) {
                    sortObjects[i] = new SortObject<>(nodes.get(i), Math.pow(distances[i], 1.38)+diffNodeAndPheromone[i]);
                }

                sortedArrayPheromoneAngle = quickSort.sort(sortObjects, 0, sortObjects.length - 1);

            } else {
                for (int i = 0; i < nodes.size(); i++) {
                    sortObjects[i] = new SortObject<>(nodes.get(i), nodes.get(i).COORDINATES.dist(targetCenter));
                }
                // Name is weird, don't think about it :)
                sortedArrayPheromoneAngle = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
            }
        }
        //System.out.println("Corresponding sorted array " + Arrays.toString(sortedArrayPheromoneAngle));
        //System.out.println("        Available Frontiers: " + Arrays.toString(graph.frontiers.getAllNodes().toArray()));
        //System.out.println("        Goal Frontier: " + sortedArrayPheromoneAngle[index].object);
        /*System.out.println("----- Chose Frontier at: " + sortedArrayPheromoneAngle[index].object);
        System.out.println("----- With Target center at: "+ targetCenter);
        System.out.println("----- Angle to Frontier: " + graph.getCurrentPosition().COORDINATES.getAngleBetweenVector(sortedArrayPheromoneAngle[index].object.COORDINATES));
        System.out.println("----- Angle difference: " + sortedArrayPheromoneAngle[index].sortParameter);*/
        return sortedArrayPheromoneAngle[index].object;
    }

    private double[] normalizeBetweenZeroAndTen(double[] array) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (double v : array) {
            if (v > max) max = v;
            if (v < min) min = v;
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = (10*(array[i]-min)/(max-min));
        }
        return array;
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
