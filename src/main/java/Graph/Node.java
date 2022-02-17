package Graph;

import java.awt.*;
import java.util.LinkedList;

public class Node {

    private LinkedList<Node> adjacentNodes;
    private LinkedList<Node> nonAdjacentNodes;
    private int relativeX; //position of the agent relative to its initial spawning position
    private int relativeY;
    private Node teleportsTo;
    //private int type;


    public Node(int x, int y) {
        LinkedList<Node> adjacentNodes = new LinkedList<Node>();
        LinkedList<Node> nonAdjacentNodes = new LinkedList<Node>(); //TODO: see if needed
        relativeX = x;
        relativeY = y;

        //Type type = checkType(relativeX, relativeY);
    }



}
