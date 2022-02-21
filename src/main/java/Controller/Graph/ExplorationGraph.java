package Controller.Graph;

import Controller.Vector2D;

public class ExplorationGraph {

    private Node origin;
    private GraphAdjacencyList list;

    public ExplorationGraph(){
        this.origin = new Node(new Vector2D(0,0));
        list = new GraphAdjacencyList();
        list.addVertex(origin);
    }

    public void createNode (Vector2D vector){
        if (!nodeExists(vector)){
            Node node =  new Node(vector);
            list.addVertex(node);
        }
    }

    private void addEdge(Node one, Node two){
        list.addEdge(one, two);
    }

    private boolean nodeExists(Vector2D vector){
        return list.isVisited(vector);
        //check the neighbours (inside the visited bucket)
    }
}
