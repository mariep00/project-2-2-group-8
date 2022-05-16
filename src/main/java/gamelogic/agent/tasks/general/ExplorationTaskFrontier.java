package gamelogic.agent.tasks.general;

import datastructures.Vector2D;
import datastructures.quicksort.QuickSort;
import datastructures.quicksort.SortObject;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.MovementController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.Tile;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.*;

@SuppressWarnings("unchecked")

public class ExplorationTaskFrontier implements TaskInterface {
    public Node goalNode;
    private Node lastNode;
    private Stack<Integer> futureMoves;
    private double orientation;
    protected ExplorationGraph graph;

    private TaskType type = TaskType.EXPLORATION;
    protected int markerThreshold = 0;
    protected int markerIndex = 0;
    protected SortObject<Node>[] sortedArray;
    protected SortObject<Node>[] sortedArrayPheromoneAngle;
    protected boolean finished = false;

    public ExplorationTaskFrontier() {
        goalNode = new Node(new Vector2D(-20000, -20000), new Tile());
    }

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            finished = false;
            futureMoves = new Stack<>();
            this.orientation = orientation;
            this.graph = graph;
            int frontierIndexToGoTo = 0;

            updateGoal(frontierIndexToGoTo, pheromoneMarkerDirection);
            boolean foundReachableNode = false;
            while (!foundReachableNode) {
                if (goalNode == lastNode) {
                    //whenStuck();
                }
                foundReachableNode = moveTo();
                if (!foundReachableNode) {
                    frontierIndexToGoTo++;
                    if (goalNode == lastNode) {whenStuck();}

                    if (sortedArray != null && frontierIndexToGoTo == sortedArray.length) {
                        markerThreshold++;
                        frontierIndexToGoTo = 0;
                    }
                    updateGoal(frontierIndexToGoTo, pheromoneMarkerDirection);
                }
            }
            //System.out.println("        " + futureMoves.toString());
            markerThreshold = 0;
            markerIndex = 0;
            sortedArray = null;
            sortedArrayPheromoneAngle = null;
        }
        //System.out.println("        CurrentPos: " + graph.getCurrentPosition().COORDINATES + " " + orientation);
        //System.out.println("        " + futureMoves.peek());
        if (futureMoves.size()==1) finished=true;
        return futureMoves.pop();
    }

    public void updateGoal(int frontierIndexToGoTo, double pheromoneMarkerDirection) {
     //Update the goal node with the  next frontier node on graph
        lastNode = goalNode;
        goalNode = getNextFrontier(frontierIndexToGoTo, pheromoneMarkerDirection);
        if (goalNode == null) {
            goalNode = graph.getTeleport();
        }
    }


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
                    sortObjects[i] = new SortObject<>(candidates.get(i), candidates.get(i).COORDINATES.dist(graph.getCurrentPosition().COORDINATES));
                }

                sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
            } else {
                for (int i = 0; i < nodes.size(); i++) {
                    sortObjects[i] = new SortObject<>(nodes.get(i), nodes.get(i).COORDINATES.dist(graph.getCurrentPosition().COORDINATES));
                }
                sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length - 1);
            }
        }

        return sortedArray[index].object;
    }

    public void whenStuck(){
        if (goalNode == lastNode){
            futureMoves.push(1);
            futureMoves.push(0);
            futureMoves.push(0);
            futureMoves.push(3);
        }
        else { System.out.print("Crap");}
    }

    public boolean moveTo() {
        
        LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), goalNode);
        if (nodesToGoal == null) return false;
        //System.out.println("        " + nodesToGoal.toString());

        futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, true);
        return true;
        //For every node in nodes to Goal
        //Check agent's positon
        //Compare agents Vector2D with nextNode Vector2D
        //Check if we are facing the next node
        //if we are, then move forward --> fill stack of future moves
        //else rotate --> fill stack of future moves
        //Once we now what we need to do to reach the new goal
        //Update stack of future moves

        //Once we reach goal node, rotate 360

    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        return other.getClass() == this.getClass();
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() { return new ExplorationTaskFrontier(); }

    @Override
    public boolean isFinished() {
        return finished;
    }


    /* nextNode Vector2D - Coordinates Vector 2D
                x=0, y=-1
    x=-1, y=0        Z       x=+1, y=0
                x=0, y=1

//0 - move forward
//1 - turn 90deg
//2 - turn 180deg
//3 - turn 270deg

//         270
//          |
// 180 ----------- 0
//          |
//          90
    * */




}
