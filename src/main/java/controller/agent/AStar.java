package controller.agent;

import java.util.ArrayList;
import java.util.LinkedList;

import controller.Vector2D;
import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.Node;

public class AStar {

    private ExplorationGraph graph;
    
    private ArrayList<ANode> open;
    private ArrayList<ANode> closed;

    private Vector2D goal;
    private ANode start;
    private ANode goalANode;

    public AStar (ExplorationGraph graph, Node startNode, Node goalNode) {
        open = new ArrayList<>();
        closed = new ArrayList<>();
        this.goal = goalNode.COORDINATES; 
        this.graph = graph;
        start = new ANode(startNode.COORDINATES);
        start.setParent(null);
        start.setG(0);
        start.setH(start.POSITION.manhattanDist(goal));
        open.add(start);
    }

    public LinkedList<Vector2D> calculate() {
        while(true) {
            ANode current = getNextFromOpen();
            open.remove(current);
            closed.add(current);

            if(current.POSITION.equals(goal)) {
                goalANode = current;
                break;
            }

            ArrayList<ANode> neighbours = getNeighbours(current);

            for (ANode node: neighbours) {
                int g = current.getG()+1;
                ANode x = isInOpen(node.POSITION);
                if (x!=null) {
                    if(g<x.getG()) {
                        x.setG(g);
                        x.setParent(current);
                    }
                } else {
                    node.setG(g);
                    node.setH(node.POSITION.manhattanDist(goal));
                    node.setParent(current);
                    open.add(node);
                }
            }
        }
        return getPath();

    }

    private LinkedList<Vector2D> getPath() {
        LinkedList<Vector2D> path = new LinkedList<>();
        ANode currentParent = goalANode;
        while (currentParent != null) {
            path.add(currentParent.POSITION);
            ANode oldNode = currentParent;
            currentParent = oldNode.parent;
        }
        return path;
    }

    private ANode isInOpen(Vector2D pos) {
        for (ANode n: open) {
            if(n.POSITION.equals(pos)) return n;
        }
        return null;
    }

    private ArrayList<ANode> getNeighbours(ANode current) {
        ArrayList<ANode> neighbours = new ArrayList<>();
        Node[] edges = graph.getNode(current.POSITION).getEdges();
        for (Node n: edges) {
            if(n!=null) {
                if(!isInClosed(n.COORDINATES)) {
                    ANode node = new ANode(n.COORDINATES);
                    neighbours.add(node);
                }
            }
        }
        return neighbours;
    }

    private boolean isInClosed(Vector2D pos) {
        for (ANode n: closed) {
            if(n.POSITION.equals(pos)) return true;
        }
        return false;
    }

    private ANode getNextFromOpen() {
        ANode next = null;
        int lowestFCost = Integer.MAX_VALUE;
        for (ANode n: open) {
            if(n.getF()<lowestFCost) {
                next = n;
                lowestFCost=n.getF();
            }
        }
        return next;
    }
}


class ANode {
    public final Vector2D POSITION;
    public ANode parent;
    private int g; //distance from starting node
    private int h; //distance from end node

    public ANode (Vector2D pos) {
        POSITION = pos;
    }

    public void setParent(ANode parent) { this.parent = parent; }

    public void setG(int g) { this.g = g; }

    public int getG() { return g; }

    public void setH(int h) { this.h = h; }

    public int getH() { return h; }

    public int getF() { return g+h; }
}