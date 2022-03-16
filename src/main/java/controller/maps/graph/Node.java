package controller.maps.graph;

import controller.Vector2D;
import controller.maps.Tile;

public class Node {

    private Node[] edges;
    private Vector2D[] neighbours;
    private Tile type;

    private boolean[] walls;

    public final Vector2D COORDINATES;

    public Node(Vector2D coordinates, Tile type){
        this.edges = new Node[5];
        this.COORDINATES = coordinates;
        this.type = type;
        this.walls = new boolean[4];
        initializeNeighbours();
    }

    private void initializeNeighbours(){
        this.neighbours = new Vector2D[4];
        this.neighbours[0] = new Vector2D(this.COORDINATES.x, (this.COORDINATES.y+1));
        this.neighbours[1] = new Vector2D((this.COORDINATES.x+1), this.COORDINATES.y);
        this.neighbours[2] = new Vector2D(this.COORDINATES.x, (this.COORDINATES.y-1));
        this.neighbours[3] = new Vector2D((this.COORDINATES.x-1), (this.COORDINATES.y));
    }

    public Vector2D[] getNeigbours () {
        return neighbours;
    }

    /**
     * Adds an edge to the vertex y into the edge list
     * @param y node
     */
    public void addEdge(Node y) {
        if (y.getType().isTeleport()) {
            edges[edges.length-1] = y;
        }
        else {
            for (int i = 0; i < edges.length-1; i++) {
                if (edges[i] == null) edges[i] = y;
            }
        }
    }

    public void addSelfEdge (int direction) {
        if (!walls[direction]) {
            walls[direction] = true;
            addEdge(this);
        }
    }

    public Node[] getEdges(){
        return this.edges;
    }

    public Tile getType() {
        return this.type;
    }

    public void setType(Tile type) {
        this.type = type;
    }

    public int getNumberOfEdges() {
        int count = 0;
        for (int i = 0; i < edges.length-1; i++) {
            Node edge = edges[i];
            if (edge != null) count++;
        }
        return count;
    }

    @Override
    public String toString() {
        return "(" + COORDINATES + " Type: " + type.getType().toString() + " Edges: " + getNumberOfEdges() + ")";
    }
}
