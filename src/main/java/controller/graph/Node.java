package controller.graph;

import controller.Tile;
import controller.Vector2D;
import java.util.LinkedList;

public class Node {

    private LinkedList<Node> edge_list;
    private Vector2D[] neighbours;
    private Tile type;

    public final Vector2D COORDINATES;

    public Node(Vector2D coords, Tile type) {
        edge_list = new LinkedList<Node>();
        COORDINATES = coords;
        this.type = type;
        initializeNeighbours();
    }

    public void addEdge(Node y)
    {
        edge_list.addFirst(y);
    }

    public void removeEdge(Node y)
    {
        for(int n=0;n<edge_list.size();n++)
        {
            if(edge_list.get(n).equals(y))
            {
                edge_list.remove(n);
            }
        }
    }

    public LinkedList<Node> getEdges(){
        return edge_list;
    }

    public Vector2D[] getNeigbours () {
        return neighbours;
    }

    private void initializeNeighbours() {
        neighbours = new Vector2D[4];
        neighbours[0] = new Vector2D(COORDINATES.x, COORDINATES.y+1);
        neighbours[1] = new Vector2D(COORDINATES.x+1, COORDINATES.y);
        neighbours[0] = new Vector2D(COORDINATES.x, COORDINATES.y-1);
        neighbours[0] = new Vector2D(COORDINATES.x-1, COORDINATES.y);
    }

    public Tile getType() {
        return type;
    }

    public void setType(Tile type) {
        this.type = type;
    }

}
