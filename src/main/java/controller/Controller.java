package controller;

import controller.agent.Agent;
import controller.maps.EndingExplorationMap;
import controller.maps.ScenarioMap;
import controller.maps.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Controller {
    
    private FOV fov;
    private ScenarioMap scMap;
    private EndingExplorationMap endingExplorationMap;
    private Vector2D[] agentSpawnLocations;
    private Vector2D[] agentPositions;
    private ArrayList<Agent> agentsGuards;
    private ArrayList<Agent> agentsIntruders;
    private double timestep;
    private double time;


    public Controller(ScenarioMap scMap) {
        this.scMap = scMap;
        agentSpawnLocations = new Vector2D[scMap.getNumGuards()];
        agentsGuards = new ArrayList<Agent>();
        agentsIntruders = new ArrayList<Agent>();
        agentPositions = new Vector2D[scMap.getNumGuards()+scMap.getNumIntruders()];
        fov = new FOV(scMap.getGuardViewRange());
        this.endingExplorationMap = new EndingExplorationMap(this.scMap);
        timestep = scMap.getTimestep();
        createAgents(1, 1);
        spawnAgents();
    }

    public void start() {
        engine();
    }

    private ArrayList<Vector2D> calculateFOV(Agent agent, Vector2D agentPosition) {
        return fov.calculate(agent.getView_angle(), agent.getView_range(), scMap.createAreaMap(agentPosition, agent.getView_range()), agent.getOrientation()).getInVision();
    }

    public void tick(double timestep) {
        for (int i=0; i<agentsGuards.size(); i++) {
            ArrayList<Vector2D> positions = calculateFOV(agentsGuards.get(i), agentPositions[i]);
            ArrayList<Tile> tiles = new ArrayList<>();
            for (Vector2D vector2D : positions) {
                //System.out.println(vector2D + ", " + agentPositions[i] + ", " + agentSpawnLocations[i] + ", " + agentsGuards.get(i).getOrientation());
                tiles.add(scMap.getTile(convertRelativeCurrentPosToAbsolute(vector2D, i)));
            }
            int task = agentsGuards.get(i).tick(tiles, positions, timestep);
            for (Vector2D vector2D : positions) {
                endingExplorationMap.updateExplorationMap(convertRelativeCurrentPosToAbsolute(vector2D, i));
            }
            updateAgent(i, task);
        }
    }

    private void updateAgent(int agentIndex, int task) {
        //0 - move forward
        //1 - turn 90deg
        //2 - turn 180deg
        //3 - turn 270deg

        switch (task) {
            case 0:
                Vector2D newPos = agentMoveForward(agentIndex);
                agentPositions[agentIndex] = newPos;
                agentsGuards.get(agentIndex).updatePosition(convertAbsoluteToRelativeSpawn(newPos, agentIndex));
                break;
            case 1:
                agentsGuards.get(agentIndex).updateOrientation(90);
                break;
            case 2:
                agentsGuards.get(agentIndex).updateOrientation(180);
                break;
            case 3:
                agentsGuards.get(agentIndex).updateOrientation(270);
                break;
        }
    }

    private Vector2D agentMoveForward(int agentIndex) {
        int numberOfSteps = getNumberOfSteps(agentIndex);
        Vector2D lastPos = agentPositions[agentIndex];
        for (int i = 1; i <= numberOfSteps; i++) {
            Vector2D pos = agentPositions[agentIndex].getSide(agentsGuards.get(agentIndex).getOrientation(), i);
            //Vector2D absolutePos = convertRelativeSpawnToAbsolute(pos, agentIndex);
            Tile tileAtPos = scMap.getTile(pos);
            if (pos.x >= scMap.getWidth() || pos.x < 0 || pos.y >= scMap.getHeight() || pos.y < 0) return lastPos;
            if (tileAtPos.isWall()) return lastPos;
            else lastPos = pos;
        }
        return lastPos;
    }

    private int getNumberOfSteps(int agentIndex) {
        double numberOfSteps = agentsGuards.get(agentIndex).getBase_speed()*timestep;
        if (numberOfSteps < 1) return 1;
        else return (int) Math.round(numberOfSteps);
    }

    private void engine(){
        while(!endingExplorationMap.isExplored()){
            tick(timestep);
            time += timestep;
        }
    }

    private void spawnAgents() {
        Random random = new Random();
        ArrayList<Integer> indicesUsed = new ArrayList<>();
        ArrayList<Vector2D> spawnAreaGuards = scMap.getSpawnAreaGuards();

        for (int i = 0; i < agentsGuards.size(); i++) {
            while (true) {
                int rand = random.nextInt(spawnAreaGuards.size());
                if (!indicesUsed.contains(rand)) {
                    indicesUsed.add(rand);
                    agentSpawnLocations[i] = spawnAreaGuards.get(rand);
                    agentPositions[i] = agentSpawnLocations[i];
                    break;
                }
            }
        }
    }

    private void createAgents(int brainIntruders, int brainGuards) {
        if (scMap.getGameMode() != 0) {
            for (int i=0; i<scMap.getNumIntruders(); i++) {
                agentsIntruders.add(new Agent(scMap.getBaseSpeedIntruder(), scMap.getSprintSpeedIntruder(), scMap.getIntruderViewAngle(), scMap.getIntruderViewRange(), 0.0, brainIntruders));
            }
        }
        
        for (int i=0; i<scMap.getNumGuards(); i++) {
            agentsGuards.add(new Agent(scMap.getBaseSpeedGuard(), 0.0, scMap.getGuardViewAngle(),scMap.getGuardViewRange(),0.0, brainGuards));
        }
    }

    private Vector2D convertRelativeSpawnToAbsolute(Vector2D relPos, int agentId) {
        return relPos.add(agentSpawnLocations[agentId]);
    }
    public ArrayList<Vector2D> convertRelativeSpawnToAbsolute(ArrayList<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeSpawnToAbsolute(vector2D, agentId));
        }
        return absPos;
    }

    public Vector2D convertAbsoluteToRelativeSpawn(Vector2D absPos, int agentId) {
        return absPos.subtract(agentSpawnLocations[agentId]);
    }

    public Vector2D convertRelativeCurrentPosToAbsolute(Vector2D relPos, int agentId) {
        return relPos.add(agentPositions[agentId]);
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToAbsolute(ArrayList<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToAbsolute(vector2D, agentId));
        }
        return absPos;
    }
}
