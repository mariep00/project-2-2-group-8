package gamelogic.agent;

import datastructures.minheap.Heap;
import datastructures.minheap.HeapItemInterface;
import datastructures.Vector2D;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public class AStar {

    private ExplorationGraph graph;
    
    //TODO: Once HashMap is generic, implement closed as HashMap
    private Heap<ANode> open;
    private ArrayList<ANode> closed;

    private Vector2D goal;
    private ANode start;
    private ANode goalANode;
    private final boolean DEBUG = true;

    public AStar (ExplorationGraph graph, Node startNode, Node goalNode) {
        open = new Heap<>(graph.getNumberOfNodes());
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
            ANode current = open.removeFirst();
            if (current == null) return null;
            if(current.POSITION.equals(goal)) {
                goalANode = current;
                break;
            }

            closed.add(current);

            ArrayList<ANode> neighbours = getNeighbours(current);

            for (ANode node: neighbours) {
                int g = current.getG()+1;
                ANode x = open.contains(node);
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
}


class ANode implements HeapItemInterface<ANode> {
    public final Vector2D POSITION;
    public ANode parent;
    private int g; //distance from starting node
    private int h; //distance from end node
    private int heapIndex;

    public ANode (Vector2D pos) {
        POSITION = pos;
        heapIndex = -1;
    }

    public void setParent(ANode parent) { this.parent = parent; }

    public void setG(int g) { this.g = g; }

    public int getG() { return g; }

    public void setH(int h) { this.h = h; }

    public int getH() { return h; }

    public int getF() { return g+h; }

    @Override
    public void setHeapIndex(int index) {
        heapIndex = index;    
    }

    @Override
    public int getHeapIndex() {
        return heapIndex;
    }

    @Override
    public int compareTo(ANode other) {
        if (getF()>other.getF()) {
            return 1;
        } else if (getF()==other.getF()) {
            if (h > other.getH()) {
                return 1;
            } else if (h == other.getH()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        ANode other = (ANode) o;
        if (POSITION.equals(other.POSITION)) {
            return true;
        } else {
            return false;
        }
    }   
}