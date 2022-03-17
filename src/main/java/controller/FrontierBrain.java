package controller;

import controller.agent.BrainInterface;
import controller.maps.*;
import controller.maps.graph.Node;
import controller.maps.graph.ExplorationGraph;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class FrontierBrain implements BrainInterface {
    private Node goalNode;
    private Node startingNode;
    private Stack<Integer> futureMoves;

    //Pass origin node, goalNode = originNode
    public FrontierBrain(){
        futureMoves = new Stack<>();
    }


    public int makeDecision(ExplorationGraph graph){
        if (futureMoves.isEmpty() /* && location == goalNode*/){
            graph.checkFrontierEdges();
            updateStart();
            updateGoal(graph);
            moveTo(graph);
        }
        return futureMoves.pop();
    }

    public void updateGoal(ExplorationGraph list){
     //Update the goal node with the  next frontier node on graph
        Node newGoalNode = list.getNextFrontier();
        goalNode = newGoalNode;
    }

    //Pass the current position
    public void updateStart(){
        //startingNode = current Node
    }


    public void moveTo(ExplorationGraph list){
        A_Star a_star = new A_Star(goalNode, startingNode);
        LinkedList<Node> nodesToGoal = a_star.calculateAstar(list);
        for(Node node: nodesToGoal){
            //int xDif = node.COORDINATES.x - ;
            //int yDif
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


    * */




}
