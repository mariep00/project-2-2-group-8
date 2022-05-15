package gamelogic.agent;

import datastructures.Vector2D;
import gamelogic.agent.brains.BrainInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.Vision;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.Tile;
import gamelogic.maps.Tile.Type;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.ArrayList;
import java.util.List;

public class Agent {

    private final double base_speed;
    private final double view_angle;
    private final double view_range;
    private double orientation;

    private final BrainInterface brain;

    public final ExplorationGraph explorationGraph;

    public Agent (double base_speed, double view_angle, double view_range, double orientation, BrainInterface brain) {
        this.base_speed = base_speed;
        this.view_angle = view_angle;
        this.view_range = view_range;
        this.orientation = orientation;
        this.brain = brain;
        explorationGraph = new ExplorationGraph();
    }

    public int tick(Vision[] inVision, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells) {
        updateGraph(inVision);
        System.out.println("Number of nodes " + brain.toString() + " " + explorationGraph.getNumberOfNodes());
        return brain.makeDecision(explorationGraph, orientation, pheromoneMarkerDirection, sounds, guardsSeen, intrudersSeen, guardYells);
    }

    private void updateGraph(Vision[] inVision) {
        ArrayList<Integer> walls = new ArrayList<>();
        for (int i=0; i<inVision.length; i++) {
            if (inVision[i].tile().getType() == Type.WALL) {
                walls.add(i);
            } else {
                explorationGraph.createNode(inVision[i].position(), inVision[i].tile());
            }
        }

        for (Integer i: walls) {
            Vector2D[] neighbours = inVision[i].position().getNeighbours();
            for (int j=0; j<neighbours.length; j++) {
                explorationGraph.addWall(neighbours[j], j);
            }
        }
    }

    public void updatePosition(Vector2D pos) {
        explorationGraph.setCurrentPosition(pos);
    }
    public void updateOrientation(double orientationToAdd) {
        double newOrientation = orientation+orientationToAdd;
        if (newOrientation > 270) orientation = newOrientation-360;
        else orientation = newOrientation;
    }

    public void changeOrientation(double newOrientation) {
        this.orientation = newOrientation;
    }

    public double getView_angle() {
        return view_angle;
    }

    public double getView_range() {
        return view_range;
    }
    public double getOrientation() { return orientation; }
    public double getBase_speed() { return base_speed; }
    public void createTeleportDestinationNode(Vector2D entrance, Vector2D destination, Tile entranceTile, Tile destinationTile) {
        Node destinationNode = explorationGraph.getNode(destination);
        if (destinationNode == null) destinationNode = explorationGraph.createNode(destination, destinationTile);

        Node entranceNode = explorationGraph.getNode(entrance);
        if (entranceNode == null) entranceNode = explorationGraph.createNode(entrance,entranceTile);

        if (!explorationGraph.teleportEdgeExistsBetween(entranceNode, destinationNode)) {
            explorationGraph.addDirectedEdge(entranceNode, destinationNode);
        }
    }

    @Override
    public String toString() {
        return "Orientation: " + orientation + ", graph: " + explorationGraph;
    }
}
