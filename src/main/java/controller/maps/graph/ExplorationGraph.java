package controller.maps.graph;

import controller.Vector2D;
import controller.maps.Tile;
import controller.maps.Tile.Type;

public class ExplorationGraph {

    private Node origin;
    private Node currentPosition;
    private GraphAdjacencyList list;

    private int testCounter = 0;

    public ExplorationGraph(){
        Tile t = new Tile();
        t.setType(Type.SPAWN_AREA_GUARDS);
        this.origin = new Node(new Vector2D(0,0), t);
        list = new GraphAdjacencyList();
        list.addVertex(origin);
        this.currentPosition = origin;
    }

    /**
     * Creates a vertex by setting its position on the map, its tile type
     * @param vector Vector2D containing the coordinates x and y
     * @param type of the Tile
     */
    public Node createNode(Vector2D vector, Tile type){
        if (!nodeExists(vector)) {
            testCounter++;
            Node node =  new Node(vector, type);
            this.list.addVertex(node);
            this.list.checkEdges(node);
            return node;
        }
        return null;
    }

    public void addWall(Vector2D pos, int direction) {
        Node node = this.list.getNode(pos);
        if (node != null)  {
            this.list.addSelfEdge(node, direction);
            this.list.checkEdges(node);
        }
        
    }

    /**
     * Checks if a vertex already exists at a given position on the map
     * @param vector Vector2D containing the coordinates x and y
     * @return true if it there is already a node
     */
    private boolean nodeExists(Vector2D vector){
        return this.list.isVisited(vector);
    }

    public Node getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Vector2D vector2D) {
        this.currentPosition = list.getNode(vector2D);
    }
    public void addEdge(Node node) {
        list.addEdge(currentPosition, node);
    }

    public Node getNode(Vector2D vector) {
        return list.getNode(vector);
    }

    @Override
    public String toString() {
        return list.toString() + " nodeExists counter: " + testCounter;
    }
}
