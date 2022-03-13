package controller.agent;

import controller.Vector2D;
import controller.maps.Tile;
import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.GraphAdjacencyList;
import controller.maps.graph.Node;

import java.util.ArrayList;
import java.util.Stack;

public class FrontierBrain implements BrainInterface {

    private Node goalNode = null;
    private Node startingNode;
    private Stack<Integer> futureMoves;
    private Agent agent;

    //Pass origin node, goalNode = originNode
    public FrontierBrain(Agent agent) {
        futureMoves = new Stack<>();
        this.agent = agent;
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

        //north frontier
        if(goalNode.COORDINATES.y < agent.getCurrentPosition().y) {
            if(agent.getOrientation() == 0.0) {
                Vector2D newPosition = new Vector2D(agent.getCurrentPosition().x, agent.getCurrentPosition().y-1);
                if(!(agent.getExplorationGraph().getListElementType(newPosition) == Tile.Type.WALL) && !(agent.getExplorationGraph().getListElementType(newPosition) == Tile.Type.TELEPORT)) {
                    return 0;
                }
                else {
                    return 1;
                }
            }
            else if(agent.getOrientation() == 90.0){
                Vector2D newPosition = new Vector2D(agent.getCurrentPosition().x, agent.getCurrentPosition().y-1);
                if(!(agent.getExplorationGraph().getListElementType(newPosition) == Tile.Type.WALL) && !(agent.getExplorationGraph().getListElementType(newPosition) == Tile.Type.TELEPORT)) {
                    return 3;
                }
                else {
                    return 0;
                }
            }
            else if(agent.getOrientation() == 180.0) {
                return 2;
            }
            else if(agent.getOrientation() == 270.0) {
                return 1;
            }
        }
        //east frontier
        else if(goalNode.COORDINATES.x < agent.getCurrentPosition().x) {
            if(agent.getOrientation() == 0.0){
                return 1;
            }
            else if(agent.getOrientation() == 90.0){
                return 0;
            }
            else if(agent.getOrientation() == 180.0) {
                return 3;
            }
            else if(agent.getOrientation() == 270.0) {
                return 2;
            }
        }
        //south frontier
        else if(goalNode.COORDINATES.y > agent.getCurrentPosition().y) {
            if(agent.getOrientation() == 0.0){
                return 2;
            }
            else if(agent.getOrientation() == 90.0){
                return 1;
            }
            else if(agent.getOrientation() == 180.0) {
                return 0;
            }
            else if(agent.getOrientation() == 270.0) {
                return 3;
            }
        }
        //west frontier
        else if(goalNode.COORDINATES.x > agent.getCurrentPosition().x) {
            if(agent.getOrientation() == 0.0){
                return 3;
            }
            else if(agent.getOrientation() == 90.0){
                return 2;
            }
            else if(agent.getOrientation() == 180.0) {
                return 1;
            }
            else if(agent.getOrientation() == 270.0) {
                return 0;
            }
        }
        return 1; //temporary
    }
}
