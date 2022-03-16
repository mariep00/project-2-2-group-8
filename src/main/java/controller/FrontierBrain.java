package controller;

import controller.graph.ExplorationGraph;
import controller.graph.GraphAdjacencyList;
import controller.graph.Node;

import java.util.ArrayList;
import java.util.Stack;

public class FrontierBrain implements BrainInterface{
    private Node goalNode;
    private Node startingNode;
    private Stack<Integer> futureMoves;

    //Pass origin node, goalNode = originNode
    public FrontierBrain(){
        futureMoves = new Stack<>();
    }


    public int makeDecision(ExplorationGraph graph){
        if (futureMoves.isEmpty() /* && location == goalNode*/){
            graph.getList().checkFrontierEdges();
            updateStart();
            updateGoal(graph.getList());
            moveTo(graph.getList());
        }
        return futureMoves.pop();
    }

    public void updateGoal(GraphAdjacencyList list){
     //Update the goal node with the  next frontier node on graph
        Node newGoalNode = list.getNextFrontier();
        goalNode = newGoalNode;
    }

    //Pass the current position
    public void updateStart(){
        //startingNode = current Node
    }


    public void moveTo(GraphAdjacencyList list){
        A_Star a_star = new A_Star(goalNode, startingNode);
        //Moving from old goal to new goal

        //Once we now what we need to do to reach the new goal
        //Update stack of future moves

        //Once we reach goal node
        list.checkFrontierEdges();
    }
}
