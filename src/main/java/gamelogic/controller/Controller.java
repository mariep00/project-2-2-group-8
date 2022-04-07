package gamelogic.controller;

import gamelogic.FOV;
import gamelogic.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.maps.*;

import java.util.*;

public class Controller {
    public final MovementController movementController;
    protected final Agent[] agents;
    protected final Random rand = new Random();
    private FOV[] fov;
    protected ScenarioMap scenarioMap;
    protected final Vector2D[] agentSpawnLocations;

    protected State currentState;
    protected State newState;

    private double timestep;
    public double time;

    private boolean finished = false;

    protected final int NUMBER_OF_GUARDS;
    protected final int NUMBER_OF_INTRUDERS;

    public Controller(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
        this.NUMBER_OF_GUARDS = scenarioMap.getNumGuards();
        this.NUMBER_OF_INTRUDERS = scenarioMap.getNumIntruders();
        this.agentSpawnLocations = new Vector2D[NUMBER_OF_GUARDS+NUMBER_OF_INTRUDERS];
        this.agents = new Agent[scenarioMap.getNumGuards()+ scenarioMap.getNumIntruders()];
        this.fov = new FOV(scenarioMap.getGuardViewRange());
        this.endingExplorationMap = new EndingExploration(this.scenarioMap);
        this.timestep = scenarioMap.getTimestep();
        this.visions = new ArrayList[scenarioMap.getNumGuards()];
        this.movementController = new MovementController();

        createAgents(1, 2);

        Vector2D[] initialPositions = spawnAgents();
        ArrayList<Vector2D>[] visions = new ArrayList[NUMBER_OF_GUARDS+NUMBER_OF_INTRUDERS];
        for (int i = 0; i < visions.length; i++) {
            visions[i] = calculateFOV(i, initialPositions[i]);
        }
        currentState = new State(initialPositions, visions, )
    }

    public void init() {
        for (int i = 0; i < agentsGuards.length; i++) {
            updateVision(i);
            updateProgress(visions[i], i); // Set the beginning "progress"
            updateMarkers();
        }
    }

    protected ArrayList<Vector2D> calculateFOV(int agentIndex, Vector2D agentPosition) {
        return fov.calculate(agentsGuards[agentIndex].getView_angle(), agentsGuards[agentIndex].getView_range(), scenarioMap.createAreaMap(agentPosition, agentsGuards[agentIndex].getView_range()), agentsGuards[agentIndex].getOrientation()).getInVision();
    }

    public void tick() {
        tick(timestep);
        time += timestep;
    }
    public void tick(double timestep) {
        for (int i=0; i<agentsGuards.length; i++) {
            ArrayList<Tile> tiles = getTilesInVision(visions[i], i);
            if (updateProgress(visions[i], i)) {    
                if(!finished) {
                    end();
                    finished = true;
                } 
                break; 
            }
            List<PheromoneMarker> pheromoneMarkersCloseEnough = getPheromoneMarkersCloseEnough(i);
            double[] pheromoneMarkerDirections = getPheromoneMarkersDirections(agentPositions[i], pheromoneMarkersCloseEnough);
            int task = agentsGuards[i].tick(tiles, convertRelativeCurrentPosToRelativeToSpawn(visions[i], i), pheromoneMarkersCloseEnough, pheromoneMarkerDirections, timestep);
            updateAgent(i, task);
            updateVision(i);
        }
        updateMarkers();
    }



    private ArrayList<Tile> getTilesInVision(ArrayList<Vector2D> positions, int agentIndex) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Vector2D vector2D : positions) {
            Vector2D abs = convertRelativeCurrentPosToAbsolute(vector2D, agentIndex);
            tiles.add(scenarioMap.getTile(abs));
        }
        return tiles;
    }

    protected void updateVision(int agentIndex) {

    }

    protected void updateAgent(int agentIndex, int task) {
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

        addMarker(agentPositions[agentIndex], new PheromoneMarker(agentsGuards[agentIndex], agentPositions[agentIndex], scenarioMap.getSmellingDistance()));
    }

    protected void addMarker(Vector2D position, MarkerInterface marker) {
        scenarioMap.getTile(position).addMarker(marker);
        Iterator<Tile> iterator = tilesWithMarker.iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            if (tile.getPheromoneMarker().getPosition().equals(position)) {
                iterator.remove();
                break;
            }
        }
        tilesWithMarker.add(scenarioMap.getTile(position));
    }

    private boolean updateProgress(Vector2D vector, int agentIndex) {
        return endingExplorationMap.updateExplorationMap(convertRelativeCurrentPosToAbsolute(vector, agentIndex));
    }
    protected boolean updateProgress(List<Vector2D> positions, int agentIndex) {
        for (Vector2D vector2D : positions) {
            if (updateProgress(vector2D, agentIndex)) {
                return true;
            }
        }
        return false;
    }


    public void engine(){
        while (!endingExplorationMap.isExplored()) {
            tick(timestep);
            time += timestep;
        }
        end();
    }

    protected void end() {
        int hours = (int) time / 3600;
        int minutes = ((int)time % 3600) / 60;
        double seconds = time % 60;
        System.out.println("Everything is explored. It took " + hours + " hour(s) " + minutes + " minutes " + seconds + " seconds.");
    }

    protected Vector2D[] spawnAgents() {
        ArrayList<Integer> indicesUsed = new ArrayList<>();
        ArrayList<Vector2D> spawnAreaGuards = scenarioMap.getSpawnAreaGuards();
        Vector2D[] agentPositions = new Vector2D[NUMBER_OF_GUARDS];

        for (int i = 0; i < NUMBER_OF_GUARDS; i++) {
            while (true) {
                int randNumber = rand.nextInt(spawnAreaGuards.size());
                if (!indicesUsed.contains(rand)) {
                    indicesUsed.add(randNumber);
                    agentSpawnLocations[i] = spawnAreaGuards.get(randNumber);
                    agentPositions[i] = agentSpawnLocations[i];
                    break;
                }
            }
        }
        return agentPositions;
    }

    private void createAgents(int brainIntruders, int brainGuards) {
        int[] orientations = {0, 90, 180, 270};
        
        for (int i = 0; i< scenarioMap.getNumGuards(); i++) {
            agents[i] = new Agent(scenarioMap.getBaseSpeedGuard(), 0.0, scenarioMap.getGuardViewAngle(), scenarioMap.getGuardViewRange(), orientations[rand.nextInt(orientations.length)], brainGuards);
        }
        for (int i = 0; i< scenarioMap.getNumIntruders(); i++) {
            agents[i+ scenarioMap.getNumGuards()] = new Agent(scenarioMap.getBaseSpeedIntruder(), scenarioMap.getSprintSpeedIntruder(), scenarioMap.getIntruderViewAngle(), scenarioMap.getIntruderViewRange(), orientations[rand.nextInt(orientations.length)], brainIntruders);
        }
    }

    public Vector2D convertRelativeSpawnToAbsolute(Vector2D relPos, int agentId) {
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
    public ArrayList<Vector2D> convertRelativeCurrentPosToAbsolute(List<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToAbsolute(vector2D, agentId));
        }
        return absPos;
    }

    public Vector2D convertRelativeCurrentPosToRelativeToSpawn(Vector2D relPos, int agentId) {
        return convertAbsoluteToRelativeSpawn(relPos.add(agentPositions[agentId]), agentId);
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToRelativeToSpawn (List<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToRelativeToSpawn(vector2D, agentId));
        }
        return absPos;
    }
}
