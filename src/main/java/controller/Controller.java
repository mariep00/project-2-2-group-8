package controller;

import controller.agent.Agent;
import controller.maps.EndingExplorationMap;
import controller.maps.ScenarioMap;
import controller.maps.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Controller {
    private final Random rand = new Random();
    private FOV fov;
    private ScenarioMap scMap;
    protected EndingExplorationMap endingExplorationMap;
    private Vector2D[] agentSpawnLocations;
    protected Vector2D[] agentPositions;
    protected Agent[] agentsGuards;
    private Agent[] agentsIntruders;
    private double timestep;
    private double time;

    public Controller(ScenarioMap scMap) {
        this.scMap = scMap;
        agentSpawnLocations = new Vector2D[scMap.getNumGuards()];
        agentsGuards = new Agent[scMap.getNumGuards()];
        agentsIntruders = new Agent[scMap.getNumIntruders()];
        agentPositions = new Vector2D[scMap.getNumGuards()+scMap.getNumIntruders()];
        fov = new FOV(scMap.getGuardViewRange());
        this.endingExplorationMap = new EndingExplorationMap(this.scMap);
        timestep = scMap.getTimestep();
        createAgents(1, 1);
    }

    public void init() {
        spawnAgents();
        for (int i = 0; i < agentsGuards.length; i++) {
            updateProgress(calculateFOV(agentsGuards[i], agentPositions[i]), i); // Set the beginning "progress"
        }
    }

    public void start() {
        engine();
    }

    private ArrayList<Vector2D> calculateFOV(Agent agent, Vector2D agentPosition) {
        return fov.calculate(agent.getView_angle(), agent.getView_range(), scMap.createAreaMap(agentPosition, agent.getView_range()), agent.getOrientation()).getInVision();
    }

    public void tick() {
        tick(timestep);
    }
    public void tick(double timestep) {
        for (int i=0; i<agentsGuards.length; i++) {
            ArrayList<Vector2D> positions = calculateFOV(agentsGuards[i], agentPositions[i]);
            ArrayList<Tile> tiles = getTilesInVision(positions, i);
            updateProgress(positions, i);
            int task = agentsGuards[i].tick(tiles, positions, timestep);
            updateAgent(i, task);
        }
    }

    private ArrayList<Tile> getTilesInVision(ArrayList<Vector2D> positions, int agentIndex) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Vector2D vector2D : positions) {
            tiles.add(scMap.getTile(convertRelativeCurrentPosToAbsolute(vector2D, agentIndex)));
        }
        return tiles;
    }

    private void updateAgent(int agentIndex, int task) {
        //0 - move forward
        //1 - turn 90deg
        //2 - turn 180deg
        //3 - turn 270deg

        switch (task) {
            case 0:
                updateAgentPosition(agentIndex, agentMoveForward(agentIndex));
                break;
            case 1:
                updateAgentOrientation(agentIndex, 90);
                break;
            case 2:
                updateAgentOrientation(agentIndex, 180);
                break;
            case 3:
                updateAgentOrientation(agentIndex,270);
                break;
        }
    }

    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        agentPositions[agentIndex] = pos;
        agentsGuards[agentIndex].updatePosition(convertAbsoluteToRelativeSpawn(pos, agentIndex));
    }
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        agentsGuards[agentIndex].updateOrientation(orientationToAdd);
    }

    protected void updateProgress(Vector2D vector, int agentIndex) {
        endingExplorationMap.updateExplorationMap(convertRelativeCurrentPosToAbsolute(vector, agentIndex));
    }
    private void updateProgress(ArrayList<Vector2D> positions, int agentIndex) {
        for (Vector2D vector2D : positions) {
            updateProgress(vector2D, agentIndex);
        }
    }

    private Vector2D agentMoveForward(int agentIndex) {
        int numberOfSteps = getNumberOfSteps(agentIndex);
        Vector2D lastPos = agentPositions[agentIndex];
        for (int i = 1; i <= numberOfSteps; i++) {
            Vector2D pos = agentPositions[agentIndex].getSide(agentsGuards[agentIndex].getOrientation(), i);
            Tile tileAtPos = scMap.getTile(pos);
            if (pos.x >= scMap.getWidth() || pos.x < 0 || pos.y >= scMap.getHeight() || pos.y < 0) return lastPos;
            if (tileAtPos.isWall()) return lastPos;
            if (isAgentAtPos(pos)) return lastPos;
            else lastPos = pos;
        }
        return lastPos;
    }

    private int getNumberOfSteps(int agentIndex) {
        double numberOfSteps = agentsGuards[agentIndex].getBase_speed()*timestep;
        if (numberOfSteps < 1) return 1;
        else return (int) Math.round(numberOfSteps);
    }

    private boolean isAgentAtPos(Vector2D pos) {
        for (int i = 0; i < agentPositions.length; i++) {
            if (agentPositions[i].equals(pos)) return true;
        }
        return false;
    }

    public void engine(){
        while(!endingExplorationMap.isExplored()){
            tick(timestep);
            time += timestep;
        }
    }

    protected void spawnAgents() {
        Random random = new Random();
        ArrayList<Integer> indicesUsed = new ArrayList<>();
        ArrayList<Vector2D> spawnAreaGuards = scMap.getSpawnAreaGuards();

        for (int i = 0; i < agentsGuards.length; i++) {
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
        int[] orientations = {0, 90, 180, 270};
        if (scMap.getGameMode() != 0) {
            for (int i=0; i<scMap.getNumIntruders(); i++) {
                agentsIntruders[i] = new Agent(scMap.getBaseSpeedIntruder(), scMap.getSprintSpeedIntruder(), scMap.getIntruderViewAngle(), scMap.getIntruderViewRange(), orientations[rand.nextInt(orientations.length)], brainIntruders);
            }
        }
        
        for (int i=0; i<scMap.getNumGuards(); i++) {
            agentsGuards[i] = new Agent(scMap.getBaseSpeedGuard(), 0.0, scMap.getGuardViewAngle(),scMap.getGuardViewRange(),orientations[rand.nextInt(orientations.length)], brainGuards);
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
