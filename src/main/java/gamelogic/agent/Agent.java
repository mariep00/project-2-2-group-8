package gamelogic.agent;

import datastructures.Vector2D;
import gamelogic.agent.brains.BrainInterface;
import gamelogic.controller.Sound;
import gamelogic.maps.Tile;
import gamelogic.maps.Tile.Type;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.ArrayList;
import java.util.List;

public class Agent {

    private final double base_speed;
    private final double sprint_speed;
    private final double view_angle;
    private final double view_range;
    private double orientation;

    private final BrainInterface brain;

    public final ExplorationGraph explorationGraph;

    public Agent (double base_speed, double sprint_speed, double view_angle, double view_range, double orientation, BrainInterface brain) {
        this.base_speed = base_speed;
        this.sprint_speed = sprint_speed;
        this.view_angle = view_angle;
        this.view_range = view_range;
        this.orientation = orientation;
        this.brain = brain;
        explorationGraph = new ExplorationGraph();
    }

    public int tick(List<Tile> inVision, List<Vector2D> coordinates, double pheromoneMarkerDirection, List<Sound> sounds) {
        updateGraph(inVision, coordinates);
        return brain.makeDecision(explorationGraph, orientation, pheromoneMarkerDirection, sounds);
    }

    private void updateGraph(List<Tile> inVision, List<Vector2D> coordinates) {
        if (inVision.size() == coordinates.size()) {
            ArrayList<Integer> walls = new ArrayList<>();
            ArrayList<Integer> notWalls = new ArrayList<>();
            for (int i=0; i<inVision.size(); i++) {
                if (inVision.get(i).getType() == Type.WALL) {
                    walls.add(i);
                } else {
                    notWalls.add(i);
                }
            }

            for (Integer i: notWalls) {
                explorationGraph.createNode(coordinates.get(i), inVision.get(i));
            }
            for (Integer i: walls) {
                Vector2D[] neighbours = coordinates.get(i).getNeighbours();
                for (int j=0; j<neighbours.length; j++) {
                    explorationGraph.addWall(neighbours[j], j);
                }
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
