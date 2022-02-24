package Controller;

import java.util.ArrayList;

import Controller.Graph.ExplorationGraph;

public class Agent {

    private int base_speed;
    private int sprint_speed;
    private double orientation;

    private BaseBrain brain;

    private ExplorationGraph map;

    public Agent (int base_speed, int sprint_speed, double orientation, int brainID) {
        this.base_speed = base_speed;
        this.sprint_speed = sprint_speed;
        this.orientation = orientation;
        initBrain(brainID);
        map = new ExplorationGraph();
    }

    public int tick(ArrayList<Tile> inVision, ArrayList<Vector2D> coordinates, double timestep) {

        updateGraph(inVision, coordinates);
        brain.makeDecision(map);
        return 1;
    }

    private void updateGraph(ArrayList<Tile> inVision, ArrayList<Vector2D> coordinates) {
        if (inVision.size() == coordinates.size()) {
            for (int i=0; i<inVision.size(); i++) {
                map.createNode(coordinates.get(i), inVision.get(i), false);
            }
        }
    }

    private void initBrain(int brainID) {

        switch (brainID) {
            case 1:
                brain = new BaseBrain();
                break;
        }
    }
}
