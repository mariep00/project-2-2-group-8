package controller;

import java.util.ArrayList;

import controller.graph.ExplorationGraph;

public class Agent {

    private int base_speed;
    private int sprint_speed;
    private double orientation;

    private BaseBrain brain;

    private ExplorationGraph map;
    private EndingExplorationMap explorationMap;

    public Agent (int base_speed, int sprint_speed, double orientation, int brainID, EndingExplorationMap explorationMap) {
        this.base_speed = base_speed;
        this.sprint_speed = sprint_speed;
        this.orientation = orientation;
        initBrain(brainID);
        map = new ExplorationGraph();
        this.explorationMap = explorationMap;
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
                explorationMap.updateExplorationMap(coordinates.get(i));
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
