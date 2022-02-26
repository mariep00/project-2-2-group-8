package Controller.Graph;

import Controller.Vector2D;
import Controller.Tile;

public class ExplorationGraph {

    private Node origin;
    private GraphAdjacencyList list;

    public ExplorationGraph(){
        this.origin = new Node(new Vector2D(0,0), new Tile(Tile.Type.SPAWNAREAGUARDS));
        this.list = new GraphAdjacencyList();
        this.list.addVertex(this.origin);
    }

    /**
     * Creates a vertex by setting its position on the map, its tile type
     * @param vector Vector2D containing the coordinates x and y
     * @param type of the Tile
     * @param hasWall setting if the newly created node has a wall
     */
    public void createNode(Vector2D vector, Tile type, boolean hasWall){
        if(!nodeExists(vector)){
            Node node =  new Node(vector, type);
            this.list.addVertex(node);
            this.list.checkEdges(node);
            if(hasWall){
                this.list.addSelfEdge(node);
            }
        }
    }

    /**
     * Checks if a vertex already exists at a given position on the map
     * @param vector Vector2D containing the coordinates x and y
     * @return true if it there is already a node
     */
    private boolean nodeExists(Vector2D vector){
        return this.list.isVisited(vector); //check the neighbours (inside the visited bucket)
    }
}
