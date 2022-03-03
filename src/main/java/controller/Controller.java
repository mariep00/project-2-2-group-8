package controller;

import java.util.ArrayList;

import controller.agent.Agent;
import controller.maps.EndingExplorationMap;
import controller.maps.ScenarioMap;

public class Controller {
    
    private FOV fov;
    private ScenarioMap scMap;
    private EndingExplorationMap explorationMap;
    private ArrayList<Vector2D> agentSpawnLocations;
    private ArrayList<Agent> agentsGuards;
    private ArrayList<Agent> agentsIntruders;
    private ArrayList<Vector2D> agentPositions;
    private double timestep;
    private double time;


    public Controller() {
        agentSpawnLocations = new ArrayList<Vector2D>();
        agentsGuards = new ArrayList<Agent>();
        agentsIntruders = new ArrayList<Agent>();
        agentPositions = new ArrayList<Vector2D>();
        fov = new FOV(10.0);
    }

    public void init(String mapPath) {
        MapBuilder build = new MapBuilder(mapPath);
        scMap = build.getMap();
        this.explorationMap = new EndingExplorationMap(this.scMap);
        timestep = scMap.getTimestep();
        createAgents(1, 1);
        spawnAgents();
    
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
        for (int i=0; i<agentsGuards.size(); i++) {
            agentsGuards.get(i).tick(null, calculateFOV(agentPositions.get(i)), timestep);

        }
    }

    private void engine(){
        while(!explorationMap.isExplored()){
            tick(timestep);
            time += timestep;
        }
    }

    private void spawnAgents() {
        //TODO: Select random spawnlocations (Vector2D) for agents in specified areas
    }

    private void createAgents(int brainIntruders, int brainGuards) {
        if (scMap.getGameMode() != 0) {
            for (int i=0; i<scMap.getNumIntruders(); i++) {
                agentsIntruders.add(new Agent(scMap.getBaseSpeedIntruder(), scMap.getSprintSpeedIntruder(), 0.0, brainIntruders, explorationMap));
            }
        }
        
        for (int i=0; i<scMap.getNumGuards(); i++) {
            agentsGuards.add(new Agent(scMap.getBaseSpeedGuard(), 0.0, 0.0, brainGuards, explorationMap));
        }
    }

}
