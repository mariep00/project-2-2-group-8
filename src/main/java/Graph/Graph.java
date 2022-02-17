package Graph;

import java.util.List;

//Graph.Graph interface
// Simplication: There are no dublicate values in T
public interface Graph<Node> {
    // whether there’s an edge from x to y
    public boolean adjacent(Node x, Node y);

    //all vertices y s.t. there’s an edge from x to y
    public List<Node> neighbors(Node x);

    //adds the vertex x
    public void addVertex(Node x);

    //removes the vertex x
    public void removeVertex(Node x);

    //adds edge from the vertices x to y
    public void addEdge(Node x, Node y);

     //removes edge from the vertices x to y
    public void removeEdge(Node x, Node y);
}
