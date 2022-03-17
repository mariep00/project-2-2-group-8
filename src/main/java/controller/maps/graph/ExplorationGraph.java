package controller.maps.graph;

import controller.Vector2D;
import controller.maps.Tile;
import controller.maps.Tile.Type;

public class ExplorationGraph {

    private Node origin;
    private Node currentPosition;
    private GraphAdjacencyList graphAdjacencyList;

    public ExplorationGraph(){
        Tile t = new Tile();
        t.setType(Type.SPAWN_AREA_GUARDS);
        this.origin = new Node(new Vector2D(0,0), t);
        graphAdjacencyList = new GraphAdjacencyList();
        graphAdjacencyList.addVertex(origin);
        this.currentPosition = origin;
    }

    /**
     * Creates a vertex by setting its position on the map, its tile type
     * @param vector Vector2D containing the coordinates x and y
     * @param type of the Tile
     */
    public Node createNode(Vector2D vector, Tile type){
        if (!nodeExists(vector)) {
            Node node = new Node(vector, type);
            this.graphAdjacencyList.addVertex(node);
            this.graphAdjacencyList.checkEdges(node);
            return node;
        }
        return null;
    }

    public void addWall(Vector2D pos, int direction) {
        Node node = this.graphAdjacencyList.getNode(pos);
        if (node != null)  {
            this.graphAdjacencyList.addSelfEdge(node, direction);
            this.graphAdjacencyList.checkEdges(node);
        }
        
    }

    /**
     * Checks if a vertex already exists at a given position on the map
     * @param vector Vector2D containing the coordinates x and y
     * @return true if it there is already a node
     */
    private boolean nodeExists(Vector2D vector){
        return this.graphAdjacencyList.isVisited(vector);
    }

    public Node getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Vector2D vector2D) {
        this.currentPosition = graphAdjacencyList.getNode(vector2D);
    }
    public void addDirectedEdge(Node node1, Node node2) {
        graphAdjacencyList.addDirectedEdge(node1, node2);
    }

    public Node getNode(Vector2D vector) {
        return graphAdjacencyList.getNode(vector);
    }

    public boolean teleportEdgeExistsBetween(Node node1, Node node2) {
        return node1.getEdges()[4] != null && node1.getEdges()[4].equals(node2);
    }

    @Override
    public String toString() {
        return graphAdjacencyList.toString();
    }
}
