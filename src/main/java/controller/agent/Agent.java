package controller.agent;

import java.util.ArrayList;

import controller.Vector2D;
import controller.maps.EndingExplorationMap;
import controller.maps.Tile;
import controller.maps.graph.ExplorationGraph;

public class Agent {

    private double base_speed;
    private double sprint_speed;
    private double orientation;

    private BaseBrain brain;

    private ExplorationGraph map;
    private EndingExplorationMap explorationMap;

    public Agent (double base_speed, double sprint_speed, double orientation, int brainID, EndingExplorationMap explorationMap) {
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
