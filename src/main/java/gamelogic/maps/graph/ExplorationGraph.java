package gamelogic.maps.graph;

import datastructures.HashMap;
import datastructures.Vector2D;
import gamelogic.maps.Tile;
import gamelogic.maps.Tile.Type;

import java.util.LinkedList;
import java.util.Random;

public class ExplorationGraph {

    private final Node origin;
    private Node currentPosition;
    private final LinkedList<Node> seenTeleports;
    private final LinkedList<Node> seenTargetArea;
    private boolean sawTargetArea;
    private final Random random;

    public final HashMap<Vector2D, Node> nodes; //all nodes
    public final HashMap<Vector2D, Node> frontiers; //nodes with less than 4 edges;

    public ExplorationGraph() {
        this.nodes = new HashMap<>(4000);
        this.frontiers = new HashMap<>(1000);
        this.seenTeleports = new LinkedList<>();
        this.seenTargetArea = new LinkedList<>();
        sawTargetArea = false;
        random = new Random();
        Tile t = new Tile();
        this.origin = new Node(new Vector2D(0, 0), t);
        addVertex(origin);
        this.currentPosition = origin;
    }

    /**
     * Returns the Node with the specified Vector2D
     *
     * @param vector
     * @return Node with the specified Vector2D
     */
    public Node getNode(Vector2D vector) {
        return nodes.getValue(vector);
    }

    /**
     * Creates a vertex by setting its position on the map, its tile type
     *
     * @param vector Vector2D containing the coordinates x and y
     * @param type   of the Tile
     */
    public Node createNode(Vector2D vector, Tile type) {
        if (!nodeExists(vector)) {
            Node node = new Node(vector, type);
            addVertex(node);
            checkEdges(node);
            if(type.getType()==Type.TELEPORT_ENTRANCE) {
                seenTeleports.add(node);
            }
            if(type.getType()==Type.TARGET_AREA) {
                seenTargetArea.add(node);
                sawTargetArea = true;
            }
            return node;
        }
        return null;
    }

    /**
     * Adds an edge to itself to represent a wall in the specified direction
     *
     * @param vector    Vector2D of the node adjacent to the wall
     * @param direction direction of the wall from the vector
     */
    public void addWall(Vector2D vector, int direction) {
        Node node = getNode(vector);
        if (node != null) {
            addSelfEdge(node, direction);
            checkEdges(node);
        }
    }

    /**
     * Checks if a vertex already exists at a given position on the map
     *
     * @param vector Vector2D containing the coordinates x and y
     * @return true if the Node exists
     */
    private boolean nodeExists(Vector2D vector) {
        return nodes.getValue(vector) != null;
    }

    public Node getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Vector2D vector2D) {
        this.currentPosition = getNode(vector2D);
    }

    /**
     * Adds an edge from node1 to node2, but not the other way around
     *
     * @param node1
     * @param node2
     */
    public void addDirectedEdge(Node node1, Node node2) {
        node1.addEdge(node2);
        updateFrontiers(node1);
    }

    /**
     * Checks if the teleport edge already exists
     *
     * @param node1 Node of the teleporter (entrance)
     * @param node2 Node of the destination of the teleport (exit)
     * @return true if the edge already exists
     */
    public boolean teleportEdgeExistsBetween(Node node1, Node node2) {
        return node1.getEdges()[4] != null && node1.getEdges()[4].equals(node2);
    }

    /**
     * Adds the vertex x to the list and potentially to the frontiers (condition on adding x to frontiers?)
     *
     * @param x node
     */
    public void addVertex(Node x) {
        this.nodes.addEntry(x.COORDINATES, x); //add or addFirst
        this.frontiers.addEntry(x.COORDINATES, x);
    }

    /**
     * Adds an edge from node1 to node2, and the other way around
     *
     * @param node1 node
     * @param node2 node
     */
    public void addUndirectedEdge(Node node1, Node node2) {
        addDirectedEdge(node1, node2);
        addDirectedEdge(node2, node1);
    }

    /**
     * Adds an edge to the vertex x itself
     *
     * @param x node
     */
    public void addSelfEdge(Node x, int direction) {
        x.addSelfEdge(direction);
        updateFrontiers(x);
    }

    /**
     * Checks if one of the node of the list has already been visited
     *
     * @param vector positions x and y of the node on the map
     * @return true if one of the nodes of the list has been visited
     */
    public boolean isVisited(Vector2D vector) {
        return nodes.getValue(vector) != null;
    }

    /**
     * Checks the edges of a newly created node and updates the frontiers w.r.t. the newly created node connections
     *
     * @param node node
     */
    public void checkEdges(Node node) {
        Vector2D[] nodeNeighbours = node.getNeigbours();
        for (int i = 0; i < nodeNeighbours.length; i++) {
            Node nodeNeighbour = nodes.getValue(nodeNeighbours[i]);
            if (nodeNeighbour != null) {
                addUndirectedEdge(node, nodeNeighbour);
            }
        }
        updateFrontiers(node);
    }

    /**
     * Updates the frontiers by removing vertices x and y only if their numbers of edges became bigger or equal to 4 respectively
     *
     * @param x node
     */
    private void updateFrontiers(Node x) {
        if (x.getNumberOfEdges() >= 4) {
            frontiers.removeEntry(x.COORDINATES);
        }
    }

    @Override
    public String toString() {
        return "Number of nodes: " + nodes.getNumberOfNodes() + " Size Frontier: " + frontiers.getNumberOfNodes();
    }

    /**
     * Returns all the vertices y s.t. there is an edge from x to y
     *
     * @param x node
     * @return a linked list containing the neighboring nodes of x
     */
    public LinkedList<Node> getNeighbours(Node x) {
        LinkedList<Node> neighbours = new LinkedList<>();
        Vector2D[] vector1 = x.getNeigbours();
        for (int i = 0; i < vector1.length; i++) {
            Node n = nodes.getValue(vector1[i]);
            if (n != null) {
                neighbours.add(n);
            }
        }
        return neighbours;
    }

    public Node getTeleport() {
        int i = random.nextInt(seenTeleports.size());
        return seenTeleports.get(i);
    }

    public LinkedList<Node> getTargetArea() {
        if (sawTargetArea) {
            return seenTargetArea;
        } else {
            return null;
        }
    }

    public int getNumberOfNodes() {
        return nodes.getNumberOfNodes();
    }
}
