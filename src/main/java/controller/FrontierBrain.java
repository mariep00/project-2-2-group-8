package controller;

import controller.graph.ExplorationGraph;
import controller.graph.GraphAdjacencyList;
import controller.graph.Node;

import java.util.Stack;

public class FrontierBrain implements BrainInterface{
    private Node goalNode;
    private Node startingNode;

    public int makeDecision(ExplorationGraph graph){
        //Select a node we want to move to (Goal)
        updateGoal(graph.getList());
        //checkRoute();
        //Check "route" to node

        // Make decision

    }

    public void updateGoal(GraphAdjacencyList list){
     //Update the goal node with the  next frontier node on graph
        Node newGoalNode = list.getNextFrontier();
        goalNode = newGoalNode;

    }

    /*
    //A* star search from startingNode to
    public void checkRoute(){
        //check the shortest path on the graph from the la
        Stack stack = new Stack();
        boolean check = true;
        while (check){
            Node currentNode = stack.pop();
            if (  == goalNode){

            }
        else{

            }
        }

     */


    }



}
