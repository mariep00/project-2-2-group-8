package controller.agent;

import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.GraphAdjacencyList;
import controller.maps.graph.Node;

import java.util.ArrayList;
import java.util.Stack;

public class FrontierBrain implements BrainInterface {

    private Node goalNode = null;
    private Node startingNode;
    private Stack<Integer> futureMoves;

    //Pass origin node, goalNode = originNode
    public FrontierBrain() {
        futureMoves = new Stack<>();
    }

    public int makeDecision(ExplorationGraph graph) {
        if(goalNode == null) {
            updateGoal(graph.getList());
            if(goalNode == null){ //when all goals reached
                return 0;
            }
        }
        return moveTo();
    }

    public void updateGoal(GraphAdjacencyList list){
        //Update the goal node with the  next frontier node on graph
        Node newGoalNode = list.getNextFrontier();
        goalNode = newGoalNode;
    }

    private int moveTo() {
        // check obstacle
        // 1 movement
        return 0;
    }
}
