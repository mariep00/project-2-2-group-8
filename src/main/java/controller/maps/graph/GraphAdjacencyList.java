package controller.maps.graph;

import controller.Vector2D;
import controller.maps.HashMap;

public class GraphAdjacencyList {

    HashMap list;
    HashMap frontiers; //nodes with less than 4 edges;

    public GraphAdjacencyList(){
        this.list = new HashMap(4000);
        this.frontiers = new HashMap(1000);
    }

    /**
     * Adds the vertex x to the list and potentially to the frontiers (condition on adding x to frontiers?)
     * @param x node
     */
    public void addVertex(Node x){
        this.list.addEntry(x.COORDINATES, x); //add or addFirst
        this.frontiers.addEntry(x.COORDINATES, x);
        //System.out.println("Frontier Size: " + frontiers.size());
    }

    /**
     * Adds an edge from vertex x to vertex y
     * @param x node
     * @param y node
     */
    public void addEdge(Node x, Node y){
        this.list.getValue(x.COORDINATES).addEdge(y);
        this.list.getValue(y.COORDINATES).addEdge(x);
        updateFrontiers(x);
        updateFrontiers(y);
    }

    /**
     * Adds an edge to the vertex x itself
     * @param x node
     */
    public void addSelfEdge(Node x, int direction){
        this.list.getValue(x.COORDINATES).addSelfEdge(direction);
        updateFrontiers(x);
    }

    /**
     * Checks if one of the node of the list has already been visited
     * @param vector positions x and y of the node on the map
     * @return true if one of the nodes of the list has been visited
     */
    public boolean isVisited(Vector2D vector){
        return list.getValue(vector) != null;
    }

    /**
     * Checks the edges of a newly created node and updates the frontiers w.r.t. the newly created node connections
     * @param node node
     */
    public void checkEdges(Node node){
        Vector2D[] nodeNeighbours = node.getNeigbours();
            for(int i=0; i < nodeNeighbours.length; i++){
                Node nodeNeighbour = frontiers.getValue(nodeNeighbours[i]);
                if (nodeNeighbour != null) {
                    addEdge(node, nodeNeighbour);
                }
            }
    }

    /**
     * Updates the frontiers by removing vertices x and y only if their numbers of edges became bigger or equal to 4 respectively
     * @param x node
     */
    private void updateFrontiers(Node x){
        if(x.getEdges().size() >= 4){
            frontiers.removeEntry(x.COORDINATES);
        }
        
    }

    public Node getNode(Vector2D vector2D) {
        return list.getValue(vector2D);
    }

    @Override
    public String toString() {
        return frontiers.toString() + " Number of Nodes: " + list.getNumberOfNodes() + " size frontier: " + frontiers.getNumberOfNodes();
    }
}