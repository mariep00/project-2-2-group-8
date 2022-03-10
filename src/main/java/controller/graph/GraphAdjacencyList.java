package controller.graph;

import controller.Vector2D;

import java.util.LinkedList;

public class GraphAdjacencyList {

    LinkedList<Node> list;
    LinkedList<Node> frontiers; //nodes with less than 4 edges;

    public GraphAdjacencyList(){
        this.list = new LinkedList<>();
        this.frontiers = new LinkedList<>();
    }

    /**
     * Checks if there is an edge from x to y
     * @param x node
     * @param y node
     * @return true if there is an edge from x to y
     */
    public boolean adjacent(Node x, Node y){
        boolean adjacent = false;
        for(int i=0; i < this.list.size(); i++){
            if(this.list.get(i).equals(x)){
                for(int j=0; j < this.list.get(i).getEdges().size(); j++){
                    if(this.list.get(i).getEdges().get(j).equals(y))
                        adjacent = true;
                }
            }
        }
        return adjacent;
    }

    /**
     * Returns all the vertices y s.t. there is an edge from x to y
     * @param x node
     * @return a linked list containing the neighboring nodes of x
     */
    public LinkedList<Node> neighbors(Node x){
        LinkedList<Node> neighbors = null;
        for(int i=0; i < this.list.size(); i++){
            if(this.list.get(i).equals(x)){
                neighbors = this.list.get(i).getEdges();
            }
        }
        return neighbors;
    }

    /**
     * Adds the vertex x to the list and potentially to the frontiers (condition on adding x to frontiers?)
     * @param x node
     */
    public void addVertex(Node x){
        this.list.addFirst(x); //add or addFirst
        this.frontiers.addFirst(x);

    }

    /**
     * Removes the vertex x from the list (from frontiers?)
     * @param x node
     */
    public void removeVertex(Node x){
        for(int i=0; i < this.list.size(); i++){
            if(this.list.get(i).equals(x)){
                while(!this.list.get(i).getEdges().isEmpty()){
                    removeEdge(this.list.get(i).getEdges().get(0), x);
                }
                this.list.remove(i);
            }
        }
    }

    /**
     * Adds an edge from vertex x to vertex y
     * @param x node
     * @param y node
     */
    public void addEdge(Node x, Node y){
        for(int i=0; i < this.list.size(); i++){
            if(this.list.get(i).equals(x)){
                this.list.get(i).addEdge(y);
                for(int j = 0; j < this.list.size(); j++){
                    if(this.list.get(j).equals(y)){
                        this.list.get(j).addEdge(x);
                    }
                }
            }
        }
        updateFrontiers(x, y);
    }

    /**
     * Adds an edge to the vertex x itself
     * @param x node
     */
    public void addSelfEdge(Node x){
        for(int i=0; i < this.list.size(); i++){
            if(this.list.get(i).equals(x)){
                this.list.get(i).addEdge(x);
            }
        }
    }

    /**
     * Removes an edge from vertex x to vertex y
     * @param x node
     * @param y node
     */
    public void removeEdge(Node x, Node y){
        for(int i=0; i < this.list.size(); i++){
            if(this.list.get(i).equals(x)){
                this.list.get(i).removeEdge(y);
                for(int j=0; j < this.list.size(); j++){
                    if(this.list.get(j).equals(y)){
                        this.list.get(j).removeEdge(x);
                    }
                }
            }
        }
    }

    /**
     * Checks if one of the node of the list has already been visited
     * @param vector positions x and y of the node on the map
     * @return true if one of the nodes of the list has been visited
     */
    public boolean isVisited(Vector2D vector){
        for(Node node: this.list){
            if(node.COORDINATES.equals(vector)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the edges of a newly created node and updates the frontiers w.r.t. the newly created node connections
     * @param node node
     */
    public void checkEdges(Node node){
        Vector2D[] nodeNeighbours = node.getNeigbours();
        for(Node n: this.frontiers){
            for(int i=0; i < nodeNeighbours.length; i++){
                if(n.COORDINATES.equals(nodeNeighbours[i])){
                    addEdge(node, n);
                }
            }
        }
    }

    /**
     * Updates the frontiers by removing vertices x and y only if their numbers of edges became bigger or equal to 4 respectively
     * @param x node
     * @param y node
     */
    private void updateFrontiers(Node x, Node y){
        if(x.getEdges().size() >= 4){
            removeVertexFromFrontiers(x);
        }
        if(y.getEdges().size() >= 4) {
            removeVertexFromFrontiers(y);
        }
    }

    /**
     * Removes a specific vertex x from the frontiers
     * @param x node
     */
    public void removeVertexFromFrontiers(Node x){
        for(int i=0; i < this.frontiers.size(); i++){
            if(this.frontiers.get(i).equals(x)){
                this.frontiers.remove(i);
            }
        }
    }

    public LinkedList<Node> getList() {
        return list;
    }

    public Node getNextFrontier() {
        return frontiers.getFirst();
    }
}