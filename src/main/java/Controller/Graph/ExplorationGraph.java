package Controller.Graph;

import Controller.Tile;
import Controller.Vector2D;
import Controller.Tile.Type;

public class ExplorationGraph {

    private Node origin;
    private GraphAdjacencyList list;

    public ExplorationGraph(){
        Tile t = new Tile();
        t.setType(Type.SPAWN_AREA_GUARDS);
        this.origin = new Node(new Vector2D(0,0), t);
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
