package controller.agent;

import controller.Vector2D;
import controller.maps.PheromoneMarker;
import controller.maps.Tile;
import controller.maps.Tile.Type;
import controller.maps.graph.ExplorationGraph;
import controller.maps.graph.Node;

import java.util.ArrayList;
import java.util.List;

public class Agent {

    private double base_speed;
    private double sprint_speed;
    private double orientation;
    private double view_angle;
    private double view_range;

    private BrainInterface brain;
    private int brainID;
    private BrainInterface frontierBrain;

    public final ExplorationGraph explorationGraph;


    public Agent (double base_speed, double sprint_speed, double view_angle, double view_range, double orientation, int brainID) {
        this.base_speed = base_speed;
        this.sprint_speed = sprint_speed;
        this.view_angle = view_angle;
        this.view_range = view_range;
        this.orientation = orientation;
        initBrain(brainID);
        this.brainID = brainID;
        explorationGraph = new ExplorationGraph();
    }

    public int tick(ArrayList<Tile> inVision, ArrayList<Vector2D> coordinates, List<PheromoneMarker> pheromoneMarkers, double[] pheromoneMarkerDirections, double timestep) {
        updateGraph(inVision, coordinates);
        return brain.makeDecision(explorationGraph, orientation);
    }

    private void updateGraph(ArrayList<Tile> inVision, ArrayList<Vector2D> coordinates) {
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

    private void initBrain(int brainID) {
        switch (brainID) {
            case 1: brain = new RandomBrain();
                break;
            case 2: brain = new FrontierBrain();
                break;
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
    public void creatTeleportDestinationNode(Vector2D entrance, Vector2D destination, Tile entranceTile, Tile destinationTile) {
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
        return "Orientation: " + orientation + ", graph: " + explorationGraph.toString();
    }

    public int getBrainID() {
        return brainID;
    }

    public Vector2D getCurrentPosition() {
        return explorationGraph.getCurrentPosition().getCOORDINATES();
    }
    //change referential
}
