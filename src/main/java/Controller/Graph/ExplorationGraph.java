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

    public void createNode (Vector2D vector, boolean hasWall){
        if (!nodeExists(vector)){
            Node node =  new Node(vector);
            list.addVertex(node);
            list.checkEdges(node);
            if (hasWall) {
                list.addSelfEdge(node);
            }
        }
    }

    private boolean nodeExists(Vector2D vector){
        return list.isVisited(vector);
        //check the neighbours (inside the visited bucket)
    }
}
