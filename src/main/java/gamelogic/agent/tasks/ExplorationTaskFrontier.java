package gamelogic.agent.tasks;

import datastructures.Vector2D;
import datastructures.quicksort.QuickSort;
import datastructures.quicksort.SortObject;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.maps.Tile;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class ExplorationTaskFrontier implements TaskInterface {
    private Node goalNode;
    private Node lastNode;
    private Stack<Integer> futureMoves;
    private double orientation;
    private ExplorationGraph graph;

    private TaskType type = TaskType.EXPLORATION;

    public ExplorationTaskFrontier(){
        
        goalNode = new Node(new Vector2D(-20000, -20000), new Tile());
    }

    public Stack<Integer> performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection){
        futureMoves = new Stack<>();
        this.orientation = orientation;
        this.graph = graph;
        int frontierIndexToGoTo = 0;
        
        updateGoal(frontierIndexToGoTo, pheromoneMarkerDirection);
        boolean foundReachableNode = false;
        while (!foundReachableNode) {
            if (goalNode == lastNode) { 
                whenStuck();
            }
            foundReachableNode = moveTo();
            if (!foundReachableNode) {
                frontierIndexToGoTo++;
                if (goalNode == lastNode) { whenStuck(); }
                updateGoal(frontierIndexToGoTo, pheromoneMarkerDirection);
            }
        }
        

        return futureMoves;
    }

    public void updateGoal(int frontierIndexToGoTo, double pheromoneMarkerDirection) {
     //Update the goal node with the  next frontier node on graph
        lastNode = goalNode;
        goalNode = getNextFrontier(frontierIndexToGoTo, pheromoneMarkerDirection);
        if (goalNode == null) {
            goalNode = graph.getTeleport();
        }
    }


    private Node getNextFrontier(int index, double pheromoneMarkerDirection) {
        LinkedList<Node> nodes = graph.frontiers.getAllNodes();
        if(nodes.isEmpty()) System.out.println("3. Nodes is empty -> mistake in get allNodes");
        SortObject<Node>[] sortObjects = new SortObject[nodes.size()];

        QuickSort<Node> quickSort = new QuickSort<>();
        SortObject<Node>[] sortedArray;

        if (pheromoneMarkerDirection != -1) {
            double[] frontierAnglesDiffPheromone = new double[nodes.size()];
            for (int i = 0; i < frontierAnglesDiffPheromone.length; i++) {
                frontierAnglesDiffPheromone[i] = Math.abs(180 - Math.abs(pheromoneMarkerDirection - graph.getCurrentPosition().COORDINATES.getAngleBetweenVector(nodes.get(i).COORDINATES)));
            }

            for (int i = 0; i < nodes.size(); i++) {
                sortObjects[i] = new SortObject<>(nodes.get(i), frontierAnglesDiffPheromone[i]);
            }

            SortObject<Node>[] sortedArrayPheromoneAngle = quickSort.sort(sortObjects, 0, sortObjects.length-1);

            ArrayList<Node> candidates = new ArrayList<>();
            double threshold = 30;
            while (candidates.size() == 0) {
                for (SortObject<Node> sortObject : sortedArrayPheromoneAngle) {
                    if (sortObject.sortParameter <= threshold) candidates.add(sortObject.object);
                    else break;
                }
                threshold += 5;
            }

            sortObjects = new SortObject[candidates.size()];
            for (int i = 0; i < candidates.size(); i++) {
                sortObjects[i] = new SortObject<>(candidates.get(i), candidates.get(i).COORDINATES.dist(graph.getCurrentPosition().COORDINATES));
            }

            sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length-1);
        }
        else {
            for (int i = 0; i < nodes.size(); i++) {
                sortObjects[i] = new SortObject<Node>(nodes.get(i), nodes.get(i).COORDINATES.dist(graph.getCurrentPosition().COORDINATES));
            }
            sortedArray = quickSort.sort(sortObjects, 0, sortObjects.length-1);
        }


        if (nodes.size() == 0) return null;
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
        Stack<Integer> temporaryStack= new Stack<>();
        AStar aStar = new AStar(graph, graph.getCurrentPosition(), goalNode);
        
        LinkedList<Vector2D> nodesToGoal = aStar.calculate();
        if (nodesToGoal == null) return false;

        Vector2D currentPos= graph.getCurrentPosition().COORDINATES;
        Iterator<Vector2D> iterator = nodesToGoal.descendingIterator();
        double current_orientation = orientation;
        while (iterator.hasNext()){
            Vector2D pos = iterator.next();
            int xDif = pos.x - currentPos.x;
            int yDif = pos.y - currentPos.y;
            if(xDif==1){
                if(current_orientation==0){
                    temporaryStack.push(0);

                }
                else if(current_orientation==90){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==180){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);

                }
                else if(current_orientation==270){
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=0;
            }
            else if(xDif==-1){
                if(current_orientation==180){
                    temporaryStack.push(0);
                }
                else if(current_orientation==270){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==0){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);

                }
                else if(current_orientation==90){ 
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=180;
            }
            else if(yDif==1){
                if(current_orientation==90){
                    temporaryStack.push(0);
                }
                else if(current_orientation==180){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==270){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);

                }
                else if(current_orientation==0){
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=90;
            }
            else if(yDif==-1){
                if(current_orientation==270){
                    temporaryStack.push(0);
                }
                else if(current_orientation==0){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==90){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);

                }
                else if(current_orientation==180){
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=270;
            }
            currentPos=pos;
        }

        temporaryStack.push(1);
        temporaryStack.push(1);
        temporaryStack.push(1);

        do{futureMoves.push(temporaryStack.pop());}
        while(!temporaryStack.isEmpty());

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
    public TaskType getType() {
        return type;
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
