package controller.maps.graph;

import controller.Vector2D;
import controller.maps.HashMap;

import java.util.Arrays;

public class GraphAdjacencyList {

    HashMap nodes;
    HashMap frontiers; //nodes with less than 4 edges;

    public GraphAdjacencyList(){
        this.nodes = new HashMap(4000);
        this.frontiers = new HashMap(1000);
    }

    /**
     * Adds the vertex x to the list and potentially to the frontiers (condition on adding x to frontiers?)
     * @param x node
     */
    public void addVertex(Node x){
        this.nodes.addEntry(x.COORDINATES, x); //add or addFirst
        this.frontiers.addEntry(x.COORDINATES, x);
    }

    /**
     * Adds an edge from node1 to node2, and the other way around
     * @param node1 node
     * @param node2 node
     */
    public void addUndirectedEdge(Node node1, Node node2){
        addDirectedEdge(node1, node2);
        addDirectedEdge(node2, node1);
    }

    /**
     * Adds an edge from node1 to node 2
     * @param node1 node
     * @param node2 node
     */
    public void addDirectedEdge(Node node1, Node node2) {
        node1.addEdge(node2);
        updateFrontiers(node1);
    }

    /**
     * Adds an edge to the vertex x itself
     * @param x node
     */
    public void addSelfEdge(Node x, int direction){
        x.addSelfEdge(direction);
        updateFrontiers(x);
    }

    /**
     * Checks if one of the node of the list has already been visited
     * @param vector positions x and y of the node on the map
     * @return true if one of the nodes of the list has been visited
     */
    public boolean isVisited(Vector2D vector){
        return nodes.getValue(vector) != null;
    }

    /**
     * Checks the edges of a newly created node and updates the frontiers w.r.t. the newly created node connections
     * @param node node
     */
    public void checkEdges(Node node){
        Vector2D[] nodeNeighbours = node.getNeigbours();
        for (int i=0; i < nodeNeighbours.length; i++) {
            Node nodeNeighbour = frontiers.getValue(nodeNeighbours[i]);
            if (nodeNeighbour != null) {
                addUndirectedEdge(node, nodeNeighbour);
            }
        }
    }

    /**
     * Updates the frontiers by removing vertices x and y only if their numbers of edges became bigger or equal to 4 respectively
     * @param x node
     */
    private void updateFrontiers(Node x){
        if (x.getNumberOfEdges() >= 4) {
            frontiers.removeEntry(x.COORDINATES);
        }
    }

    public Node getNode(Vector2D vector2D) {
        return nodes.getValue(vector2D);
    }

    @Override
    public String toString() {
        return frontiers.toString() + " Number of Nodes: " + nodes.getNumberOfNodes() + " size frontier: " + frontiers.getNumberOfNodes() + ", " + Arrays.toString(nodes.getValue(new Vector2D(88, 48)).getEdges());
    }
}