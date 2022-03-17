package controller;


import controller.maps.Tile;
import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public class A_Star {

    private Node startingNode;
    private Node goalNode;
    private Node currentNode;

    // g = distance to starting node
    // h = manhattan distance (DISTANCE TO GOAL NODE)

    //Maybe this have to be LinkedLists
    private ArrayList<Node> open;
    private ArrayList<Node> closed;
    private ArrayList<Node> parent;

    private LinkedList<Node> path;


    public A_Star(Node startingNode, Node goalNode){
        this.startingNode = startingNode;
        this.goalNode = goalNode;
        currentNode = startingNode;
        open = new ArrayList<>();
        closed = new ArrayList<>();
        path = new LinkedList<>();

        open.add(this.startingNode);
        startingNode.setParent(null);
    }

    public LinkedList<Node> calculateAstar(ExplorationGraph graph){
        while (!path.contains(goalNode)){
            // add the adjacent vertices of current node to OPEN
            if (open.size() == 1 && open.contains(startingNode)) {
                ArrayList<Node> neighboursList = new ArrayList<Node>(graph.getNeighbours(startingNode));
                checkNeighbours(neighboursList, startingNode);
            }
            else {
                newCurrentNode();
                if (currentNode == goalNode){
                    checkPath();
                    return path;
                }
                open.remove(currentNode);

                closed.add(currentNode);
                ArrayList<Node> neighboursList = new ArrayList<Node>(graph.getNeighbours(currentNode));
                checkNeighbours(neighboursList, currentNode);
            }

            }
        return null;
    }

    public void checkNeighbours(ArrayList<Node> neighboursList, Node parent){
        for (Node neighbour:neighboursList) {
            if (!(neighbour.getTile().getType().equals(Tile.Type.WALL)) || closed.contains(neighbour)){
                continue;
            }
            else if (/* new path is shorter*/ false || !open.contains(neighbour)){
                neighbour.updateG();
                neighbour.updateH(goalNode);
                // Update Linked List (Set parent of neighbour to current)
                if (!open.contains(neighbour)){
                    open.add(neighbour);
                    neighbour.setParent(parent);
                }
            }
        }
    }

    public void newCurrentNode(){
        Node temporal = open.get(0);
        for (Node node : open){
            if ((node.getG() + node.getH()) > (temporal.getG() + temporal.getH())){
                temporal = node;
            }
            else continue;
        }
        currentNode = temporal;
    }

    public void checkPath(){
        path.add(goalNode);
        while(!path.contains(startingNode)){
            Node temporalNode = path.getLast().getParent();
            path.add(temporalNode);
        }
    }

}
