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

    public void updateStart(){

    }


    public void moveTo(GraphAdjacencyList list){
        //Moving from old goal to new goal

        //Once we now what we need to do to reach the new goal
        //Update stack of future moves

        //Once we reach goal node
        list.checkFrontierEdges();
    }
}
