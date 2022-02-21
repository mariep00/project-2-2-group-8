package Controller.Graph;

import java.util.LinkedList;

public class GraphAdjacencyList<Node> implements Graph<Node> {

    LinkedList<Vertices<Node>> list;

    public GraphAdjacencyList()
    {
        list = new LinkedList<Vertices<Node>>();
    }

    // whether there’s an edge from x to y
    public boolean adjacent(Node x, Node y)
    {
        boolean adj = false;
        for(int i = 0; i< list.size(); i++)
        {
            if(list.get(i).getElement().equals(x))
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
            if(list.get(l).getElement().equals(x))
            {
                nb = list.get(l).getEdges();
            }
        }
        return nb;
    }

    //adds the vertex x
    public void addVertex(Node x)
    {
        list.addFirst(new Vertices<Node>(x)); //add or addFirst
    }

    //removes the vertex x
    public void removeVertex(Node x)
    {
        for(int k=0;k< list.size(); k++)
        {
            if (list.get(k).getElement().equals(x))
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
            if(list.get(o).getElement().equals(x)) {
                list.get(o).addEdge(y);
                for (int p = 0; p < list.size(); p++) {
                    if (list.get(p).getElement().equals(y)) {
                        list.get(p).addEdge(x);
                    }
                }

            }
        }
    }

    //removes edge from the vertices x to y
    public void removeEdge(Node x, Node y)
    {
        for(int o = 0;o<list.size();o++)
        {
            if (list.get(o).getElement().equals(x))
            {
                list.get(o).removeEdge(y);
                for (int p = 0; p < list.size(); p++)
                {
                    if (list.get(p).getElement().equals(y))
                    {
                        list.get(p).removeEdge(x);
                    }
                }
            }
        }
    }


    class Vertices<Node>
    {
        LinkedList<Node> edge_list;
        Node element;
        public Vertices(Node e)
        {
            edge_list = new LinkedList<Node>();
            element=e;
        }

        public LinkedList<Node> getEdges(){
            return edge_list;
        }
        
        public Node getElement()
        {
            return element;
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
    }
}