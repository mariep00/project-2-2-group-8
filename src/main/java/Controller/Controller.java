package Controller;

import java.util.ArrayList;

public class Controller {
    
    private FOV fov;
    private ScenarioMap scMap;
    ArrayList<Vector2D> agentSpawnLocations;
    ArrayList<BaseAgent> agents;
    ArrayList<Vector2D> agentPositions;
    private boolean explored; //TODO ending condition
    private double timestep;


    public Controller () {
        agentSpawnLocations = new ArrayList<Vector2D>();
        agents = new ArrayList<BaseAgent>();
        agentPositions = new ArrayList<Vector2D>();
        timestep = scMap.getTimestep();
    }

    public void init () {

    }

    private ArrayList<Vector2D> calculateFOV(Vector2D agentPosition) {
        return null;
    }

    private Vector2D translatePosition(Vector2D relPos, int agentId) {
        int x = agentSpawnLocations.get(agentId).x+relPos.x;
        int y = agentSpawnLocations.get(agentId).y+relPos.y;
        return new Vector2D(x, y);
    }

    public void tick() {
        for (int i=0; i<agents.size(); i++) {
            agents.get(i).tick(calculateFOV(agentPositions.get(i)));

        }
    }

    public void engine(double timestep){
        while (!explored){
            tick();

        }

    }

}
