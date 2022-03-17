package controller.maps.graph;

import controller.Vector2D;
import controller.maps.TeleportExit;
import controller.maps.Tile;

public class Node {

    private Node[] edges;
    private Vector2D[] neighbours;
    private Tile tile;

    private boolean[] walls;

    public final Vector2D COORDINATES;

    public Node(Vector2D coordinates, Tile tile){
        this.edges = new Node[5];
        this.COORDINATES = coordinates;
        this.tile = tile;
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
     * @param other node
     */
    public void addEdge(Node other) {
        if (tile.isTeleportEntrance() && other.tile.isTeleportExit() && ((TeleportExit) other.tile.getFeature()).entrance == tile.getFeature()) {
            edges[edges.length - 1] = other;
        }
        else {
            for (int i = 0; i < edges.length-1; i++) {
                if (edges[i] == null) {
                    edges[i] = other;
                    break;
                }
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

    public Tile getTile() {
        return this.tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getNumberOfEdges() {
        int count = 0;
        for (int i = 0; i < edges.length-1; i++) {
            Node edge = edges[i];
            if (edge != null) count++;
        }
        return count;
    }

    public boolean equals(Node other) {
        return COORDINATES.equals(other.COORDINATES) && tile.equals(other.tile);
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder(COORDINATES + " Tile: " + tile + ", Number of edges: " + getNumberOfEdges() + ", Edges: [");
        for (int i = 0; i < edges.length; i++) {
            toReturn.append(edges[i] == null ? "null" : edges[i].COORDINATES.toString());
            if (i < edges.length-1) toReturn.append(", ");
            else toReturn.append("]");
        }
        return toReturn.toString();
    }
}
