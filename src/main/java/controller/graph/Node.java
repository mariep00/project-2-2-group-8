package controller.graph;

import controller.Tile;
import controller.Vector2D;
import java.util.LinkedList;

public class Node {

    private LinkedList<Node> edge_list;
    private Vector2D[] neighbours;
    private Tile type;

    // We need this for the A*
    private int g; //distance to starting node
    private int h; //distance to goal node (Manhattan distance)

    public final Vector2D COORDINATES;

    public Node(Vector2D coordinates, Tile type){
        this.edge_list = new LinkedList<>();
        this.COORDINATES = coordinates;
        this.type = type;
        initializeNeighbours();

    }

    private void initializeNeighbours(){
        this.neighbours = new Vector2D[4];
        this.neighbours[0] = new Vector2D(this.COORDINATES.x, (this.COORDINATES.y+1));
        this.neighbours[1] = new Vector2D((this.COORDINATES.x+1), this.COORDINATES.y);
        this.neighbours[2] = new Vector2D(this.COORDINATES.x, (this.COORDINATES.y-1));
        this.neighbours[3] = new Vector2D((this.COORDINATES.x-1), (this.COORDINATES.y));
    }

    public Vector2D[] getNeighbours() {
        return neighbours;
    }

    /**
     * Adds an edge to the vertex y into the edge list
     * @param y node
     */
    public void addEdge(Node y)
    {
        this.edge_list.addFirst(y);
    }

    /**
     * Removes all edges to the vertex y from the edge list
     * @param y node
     */
    public void removeEdge(Node y){
        for(int i=0; i < this.edge_list.size(); i++){
            if(this.edge_list.get(i).equals(y)){
                this.edge_list.remove(i);
            }
        }
    }

    public LinkedList<Node> getEdges(){
        return this.edge_list;
    }

    public Tile getType() {
        return this.type;
    }

    public void setType(Tile type) {
        this.type = type;
    }

    public void updateG(){
        g ++;
    }

    public void updateH(Node goalNode){
        h = Math.abs(goalNode.COORDINATES.x - this.COORDINATES.x) + Math.abs(goalNode.COORDINATES.y - this.COORDINATES.y);
    }

    public int getH(){ return h;}

    public int getG(){ return g;}
}
