package Controller.Graph;

import Controller.Vector2D;

public class ExplorationGraph {

    private Node origin;
    private GraphAdjacencyList list;

    public ExplorationGraph(){
        this.origin = new Node(0,0);
        list = new GraphAdjacencyList();
        list.addVertex(origin);
    }

    public void createNode (Vector2D vector){
        Node node =  new Node(vector.getX(), vector.getY());
        list.addVertex(node);
    }

    private void addEdge(Node one, Node two){
        list.addEdge(one, two);
    }



}
