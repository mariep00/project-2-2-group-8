package gamelogic.agent;

import datastructures.HashMap;
import datastructures.Vector2D;
import datastructures.minheap.Heap;
import datastructures.minheap.HeapItemInterface;
import gamelogic.maps.Tile.Type;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public class AStar {

    public static LinkedList<Vector2D> calculate(ExplorationGraph graph, Node startNode, Node goalNode) {
        return calculate(graph, startNode, goalNode, -1);
    }

    public static LinkedList<Vector2D> calculate(ExplorationGraph graph, Node startNode, Node goalNode, int upperBound) {
        Heap<ANode> open = new Heap<>(graph.getNumberOfNodes());
        HashMap<Vector2D, ANode> closed = new HashMap<>(3000);
        Vector2D goal = goalNode.COORDINATES; 
        ANode start = new ANode(startNode.COORDINATES);
        ANode goalANode = null;
        start.setParent(null);
        start.setG(0);
        start.setH(start.POSITION.manhattanDist(goal));
        open.add(start);
        ANode lastNode = null;
        boolean isFinished = false;
        
        while(true) {

            if (open.isEmpty() && upperBound == -1) return null;
            if (isFinished) break;
            ANode current = open.removeFirst();

            if(current.POSITION.equals(goal)) {
                goalANode = current;
                break;
            }

            closed.addEntry(current.POSITION, current);

            ArrayList<ANode> neighbours = getNeighbours(graph, current, closed);
            for (ANode node: neighbours) {
                int g = current.getG()+1;
                ANode x = open.contains(node);
                if (x!=null) {
                    if(g<x.getG()) {
                        x.setG(g);
                        x.setParent(current);
                    }
                } else if(upperBound == -1 || g <= upperBound) {
                    node.setG(g);
                    node.setH(node.POSITION.manhattanDist(goal));
                    node.setParent(current);
                    open.add(node);
                    lastNode = node;
                } else { isFinished = true; }
            }
        }

        LinkedList<Vector2D> path = new LinkedList<>();
        ANode currentParent = goalANode;
        if (upperBound != -1) { currentParent = lastNode; }
        
        while (currentParent != null) {
            path.add(currentParent.POSITION);
            ANode oldNode = currentParent;
            currentParent = oldNode.parent;
        }
        
        return path;

    }

    private static ArrayList<ANode> getNeighbours(ExplorationGraph graph, ANode current, HashMap<Vector2D, ANode> closed) {
        ArrayList<ANode> neighbours = new ArrayList<>();
        Node currentNode = graph.getNode(current.POSITION);
        Node[] edges = currentNode.getEdges();
        
        if (currentNode.getTile().getType().equals(Type.TELEPORT_ENTRANCE)) {
            Node exit = edges[edges.length-1];
            if (exit != null) {
                if(closed.getValue(exit.COORDINATES) == null) {
                    ANode node = new ANode(exit.COORDINATES);
                    neighbours.add(node);
                }
            }  
        } else {
            for (Node n: edges) {
                if(n!=null) {
                    if(closed.getValue(n.COORDINATES) == null) {
                        ANode node = new ANode(n.COORDINATES);
                        neighbours.add(node);
                    }
                }
            }
        }
        return neighbours;
    }

    public static boolean pathReachedGoal(LinkedList<Vector2D> path, Vector2D goal) {
        return path.getLast().equals(goal);
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
        return POSITION.equals(other.POSITION);
    }   
}