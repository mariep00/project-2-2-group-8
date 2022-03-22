package controller.agent;

import controller.Vector2D;
import controller.maps.Tile;
import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class FrontierBrain implements BrainInterface {
    private Node goalNode;
    private Node lastNode;
    private Stack<Integer> future_moves;
    private double orientation;
    private ExplorationGraph graph;

    public FrontierBrain(){
        future_moves = new Stack<>();
        goalNode = new Node(new Vector2D(-20000, -20000), new Tile());
    }

    public int makeDecision(ExplorationGraph graph, double orientation){
        this.orientation = orientation;
        this.graph = graph;
        int frontierIndexToGoTo = 0;
        if (future_moves.isEmpty()){
            
            updateGoal(frontierIndexToGoTo);
            
            boolean foundReachableNode = false;
            while (!foundReachableNode) {
                if (goalNode == lastNode) { 
                    whenStuck(); 
                    break;
                }
                foundReachableNode = moveTo();
                if (!foundReachableNode) {
                    frontierIndexToGoTo++;
                    if (goalNode == lastNode) { whenStuck(); }
                    updateGoal(frontierIndexToGoTo);
                }
            }
        }

        return future_moves.pop();
    }

    public void updateGoal(int frontierIndexToGoTo) {
     //Update the goal node with the  next frontier node on graph
        lastNode=goalNode;
        goalNode= graph.getNextFrontier(frontierIndexToGoTo);
        if (goalNode == null) {
            goalNode = graph.getTeleport();
        }
    }

    public void whenStuck(){
        if (goalNode == lastNode){
            future_moves.push(1);
            future_moves.push(0);
            future_moves.push(0);
            future_moves.push(3);
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

        do{future_moves.push(temporaryStack.pop());}
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
