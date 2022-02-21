package Controller.Graph;

import Controller.Vector2D;
import java.util.LinkedList;

public class Node {

    private LinkedList<Node> edge_list;
    private Node teleportsTo;
    public final Vector2D COORDINATES;
    //private int type;


    public Node(Vector2D coords) {
        edge_list = new LinkedList<Node>();
        COORDINATES = coords;
    }

    public void addEdge(Node y)
    {
        edge_list.addFirst(y);
    }

    public void removeEdge(Node y)
    {
        for(int n=0;n<edge_list.size();n++)
        {
            if(edge_list.get(n).equals(y))
            {
                edge_list.remove(n);
            }
        }
    }

    public LinkedList<Node> getEdges(){
        return edge_list;
    }





}
