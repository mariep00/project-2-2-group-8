package controller;

import java.util.ArrayList;

public class Controller {
    
    private FOV fov;
    private ScenarioMap scMap;
    private EndingExplorationMap explorationMap;
    ArrayList<Vector2D> agentSpawnLocations;
    ArrayList<Agent> agents;
    ArrayList<Vector2D> agentPositions;
    private double timestep;
    private double time;


    public Controller() {
        agentSpawnLocations = new ArrayList<Vector2D>();
        agents = new ArrayList<Agent>();
        agentPositions = new ArrayList<Vector2D>();
        this.scMap = new ScenarioMap();
        this.explorationMap = new EndingExplorationMap(this.scMap);
        timestep = scMap.getTimestep();
        fov = new FOV(10.0);
    }


    public void start() {
        engine();
    }

    private ArrayList<Vector2D> calculateFOV(Vector2D agentPosition) {
        return null;
    }

    private Vector2D translatePosition(Vector2D relPos, int agentId) {
        int x = agentSpawnLocations.get(agentId).x+relPos.x;
        int y = agentSpawnLocations.get(agentId).y+relPos.y;
        return new Vector2D(x, y);
    }

    public void tick(double timestep) {
        for (int i=0; i<agents.size(); i++) {
            agents.get(i).tick(null, calculateFOV(agentPositions.get(i)), timestep);

        }
    }

    public void engine(){
        while(!explorationMap.isExplored()){
            tick(timestep);
            time += timestep;
        }
    }
}
