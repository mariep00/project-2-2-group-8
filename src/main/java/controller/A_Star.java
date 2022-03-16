package controller;

import controller.graph.GraphAdjacencyList;
import controller.graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public class A_Star {

    private Node startingNode;
    private Node goalNode;
    private Node currentNode;
    private Tree tree;

    // g = distance to starting node
    // h = manhattan distance (DISTANCE TO GOAL NODE)

    private ArrayList<Node> open;
    private ArrayList<Node> closed;
    private ArrayList<Node> finalPath;


    public A_Star(Node startingNode, Node goalNode){
        this.startingNode = startingNode;
        this.goalNode = goalNode;
        currentNode = startingNode;
        open = new ArrayList<>();
        closed = new ArrayList<>();

        open.add(this.startingNode);
    }

    public ArrayList<Node> calculateAstar(GraphAdjacencyList graph){
        while (!tree.contains(goalNode)){

            // add the adjacent vertices of current node to OPEN
            ArrayList<Node> neighboursList = new ArrayList<>(graph.neighbors(startingNode));
            open.addAll(neighboursList);

            // remove current from OPEN
            open.remove(currentNode);

            //for every node in current.adjacencyList()
            //      if (closed.contains(node)) -> continue
            //
            for (Node neighbour:neighboursList) {
                if (!(neighbour.getType().equals(Tile.Type.WALL))){

                }

            }

            //We have tree inside this loop
        }


        //checkTree(); --> update final path

        return finalPath;
    }



}
