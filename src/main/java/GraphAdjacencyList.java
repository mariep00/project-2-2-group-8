import java.util.LinkedList;

public class GraphAdjacencyList<T> implements Graph<T> {

    LinkedList<Vertices<T>> list;

    public GraphAdjacencyList()
    {
        list = new LinkedList<Vertices<T>>();
    }

    // whether there’s an edge from x to y
    public boolean adjacent(T x, T y)
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

    //all vertices y s.t. there’s an edge from x to y
    public LinkedList<T> neighbors(T x)
    {
        LinkedList<T> nb = null;

        for (int l=0; l< list.size(); l++)
        {
            if(list.get(l).getElement().equals(x))
            {
                nb = list.get(l).getEdges();
            }
        }
        return nb;
    }

    //adds the vertex x
    public void addVertex(T x)
    {
        list.addFirst(new Vertices<T>(x)); //add or addFirst
    }

    //removes the vertex x
    public void removeVertex(T x)
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

    //adds edge from the vertices x to y
    public void addEdge(T x, T y)
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

    //removes edge from the vertices x to y
    public void removeEdge(T x, T y)
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


    class Vertices<T>
    {
        LinkedList<T> edge_list;
        T element;
        public Vertices(T e)
        {
            edge_list = new LinkedList<T>();
            element=e;
        }

        public LinkedList<T> getEdges(){
            return edge_list;
        }
        
        public T getElement()
        {
            return element;
        }

        public void addEdge(T y)
        {
            edge_list.addFirst(y);
        }

        public void removeEdge(T y)
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