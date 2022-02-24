package Controller.Graph;

import Controller.Tile;
import Controller.Vector2D;

public class ExplorationGraph {

    private Node origin;
    private GraphAdjacencyList list;

    public ExplorationGraph(){
        this.origin = new Node(new Vector2D(0,0), new Tile(Tile.Type.SPAWNAREAGUARDS));
        list = new GraphAdjacencyList();
        list.addVertex(origin);
    }

    public void createNode (Vector2D vector, Tile type, boolean hasWall){
        if (!nodeExists(vector)){
            Node node =  new Node(vector, type);
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
