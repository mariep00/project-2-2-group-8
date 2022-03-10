package controller.agent;

import controller.Vector2D;
import controller.maps.Tile;
import controller.maps.graph.ExplorationGraph;

import java.util.ArrayList;

public class Agent {

    private double base_speed;
    private double sprint_speed;
    private double orientation;
    private double view_angle;
    private double view_range;

    private BrainInterface brain;

    private ExplorationGraph explorationGraph;

    public Agent (double base_speed, double sprint_speed, double view_angle, double view_range, double orientation, int brainID) {
        this.base_speed = base_speed;
        this.sprint_speed = sprint_speed;
        this.view_angle = view_angle;
        this.view_range = view_range;
        this.orientation = orientation;
        initBrain(brainID);
        explorationGraph = new ExplorationGraph();
    }

    public int tick(ArrayList<Tile> inVision, ArrayList<Vector2D> coordinates, double timestep) {
        updateGraph(inVision, coordinates);
        return brain.makeDecision(explorationGraph);
    }

    private void updateGraph(ArrayList<Tile> inVision, ArrayList<Vector2D> coordinates) {
        if (inVision.size() == coordinates.size()) {
            for (int i=0; i<inVision.size(); i++) {
                explorationGraph.createNode(coordinates.get(i), inVision.get(i), false);
            }
        }
    }

    private void initBrain(int brainID) {
        switch (brainID) {
            case 1:
                brain = new RandomBrain();
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

    public double getView_angle() {
        return view_angle;
    }

    public double getView_range() {
        return view_range;
    }
    public double getOrientation() { return orientation; }
    public double getBase_speed() { return base_speed; }
}
