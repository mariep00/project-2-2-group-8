package Controller.Graph;

import Controller.Vector2D;

import java.util.LinkedList;

public class GraphAdjacencyList  {

    LinkedList<Node> list;
    LinkedList<Node> frontiers; //nodes with less than 4 edges;

    public GraphAdjacencyList()
    {
        list = new LinkedList<Node>();
        frontiers = new LinkedList<Node>();
    }

    // whether there’s an edge from x to y
    public boolean adjacent(Node x, Node y)
    {
        boolean adj = false;
        for(int i = 0; i< list.size(); i++)
        {
            if(list.get(i).equals(x))
            {
                for (int j=0; j<list.get(i).getEdges().size(); j++)
                {
                    if(list.get(i).getEdges().get(j).equals(y))
                        adj = true;
                }
            }
        }
        return adj;
    }

    //all vertices y s.t. there’s an edge from x to y
    public LinkedList<Node> neighbors(Node x)
    {
        LinkedList<Node> nb = null;

        for (int l=0; l< list.size(); l++)
        {
            if(list.get(l).equals(x))
            {
                nb = list.get(l).getEdges();
            }
        }
        return nb;
    }

    //adds the vertex x
    public void addVertex(Node x)
    {
        list.addFirst(x); //add or addFirst
        frontiers.addFirst(x);

    }

    //removes the vertex x
    public void removeVertex(Node x)
    {
        for(int k=0;k< list.size(); k++)
        {
            if (list.get(k).equals(x))
            {
                while(!list.get(k).getEdges().isEmpty())
                {
                    removeEdge(list.get(k).getEdges().get(0), x);
                }
                list.remove(k);
            }
        }
    }

    //adds edge from the vertices x to y
    public void addEdge(Node x, Node y)
    {
        for(int o = 0;o<list.size();o++)
        {
            if(list.get(o).equals(x)) {
                list.get(o).addEdge(y);
                for (int p = 0; p < list.size(); p++) {
                    if (list.get(p).equals(y)) {
                        list.get(p).addEdge(x);
                    }
                }

            }
        }
        updateFrontiers(x, y);
    }

    public void addSelfEdge (Node x) {
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).equals(x)) {
                list.get(i).addEdge(x);
            }
        }
    }

    //removes edge from the vertices x to y
    public void removeEdge(Node x, Node y)
    {
        for(int o = 0;o<list.size();o++)
        {
            if (list.get(o).equals(x))
            {
                list.get(o).removeEdge(y);
                for (int p = 0; p < list.size(); p++)
                {
                    if (list.get(p).equals(y))
                    {
                        list.get(p).removeEdge(x);
                    }
                }
            }
        }
    }

    public boolean isVisited(Vector2D vector){
        for (Node node: list){
            if (node.COORDINATES.equals(vector)){
                return true;
            }
        }
        return false;
    }

    public void checkEdges (Node node) {
        Vector2D[] nodeNeighbours = node.getNeigbours();
        for (Node n: frontiers) {
            for (int i=0; i<nodeNeighbours.length; i++) {
                if (n.COORDINATES.equals(nodeNeighbours[i])) {
                    addEdge(node, n);
                }
            }
        }
    }

    private void updateFrontiers (Node x, Node y) {
        if (x.getEdges().size() >= 4) {
            removeVertexFromFrontiers(x);
        }
        if (y.getEdges().size() >= 4) {
            removeVertexFromFrontiers(y);
        }
    }

    public void removeVertexFromFrontiers(Node x)
    {
        for(int k=0;k< frontiers.size(); k++)
        {
            if (frontiers.get(k).equals(x))
            {
                frontiers.remove(k);
            }
        }
    }
}