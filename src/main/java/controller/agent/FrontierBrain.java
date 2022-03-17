package controller.agent;

import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class FrontierBrain implements BrainInterface {
    private Node goalNode;
    private Node startingNode;
    private Stack<Integer> future_moves;
    private double orientation;
    private ExplorationGraph graph;


    //Pass origin node, goalNode = originNode
    public FrontierBrain(){
        futureMoves = new Stack<>();
    }


    public int makeDecision(ExplorationGraph graph, double orientation){
        this.orientation = orientation;
        this.graph=graph;
        if (future_moves.isEmpty() /* && location == goalNode*/){
            updateStart();
            updateGoal();
            moveTo(graph);
        }

        return future_moves.pop();
    }

    public void updateGoal(){
     //Update the goal node with the  next frontier node on graph
        Node newGoalNode = graph.getNextFrontier();
        goalNode = newGoalNode;
    }

    //Pass the current position
    public void updateStart(){
        //startingNode = current Node
    }


    public void moveTo(ExplorationGraph list){
        A_Star a_star = new A_Star(goalNode, startingNode);
        LinkedList<Node> nodesToGoal = a_star.calculateAstar(explorationGraph);
        Node current_node= explorationGraph.getCurrentPosition();
        Iterator<Node> iterator = nodesToGoal.descendingIterator();
        double current_orientation = orientation;
        while (iterator.hasNext()){
            Node node = iterator.next();
            int xDif = node.COORDINATES.x - current_node.COORDINATES.x;
            int yDif = node.COORDINATES.y - current_node.COORDINATES.y;
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
                else if(current_orientation==90){ //TODO check statement always false
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
            current_node=node;
        }

        //For every node in nodes to Goal
                //Check agent's positon
                //Compare agents Vector2D with nextNode Vector2D
                //Check if we are facing the next node
                // if we are, then move forward --> fill stack of future moves
                // else rotate --> fill stack of future moves
        //Once we now what we need to do to reach the new goal
        //Update stack of future moves

        //Once we reach goal node
        list.checkFrontierEdges();
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
