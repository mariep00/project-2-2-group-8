package gamelogic.controller;

import gamelogic.FOV;
import gamelogic.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.maps.ScenarioMap;
import gamelogic.maps.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Abstract because you should not create a Controller class, but either a ControllerExploration or ControllerSurveillance class
public abstract class Controller {
    protected final Random rand = new Random();

    public final MovementController movementController;
    public final MarkerController markerController;
    protected final ScenarioMap scenarioMap;
    protected final EndingExploration endingExploration;
    protected final int numberOfGuards;
    protected final int numberOfIntruders;
    protected final Agent[] agents;
    protected final Vector2D[] agentSpawnLocations;
    protected FOV fov; // TODO Should become an array, such that every agent has their own object. Or make it s.t. FOV does not store any variables that change for each agent

    protected State currentState;
    protected State nextState;

    protected double timestep;
    public double time;

    public Controller(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
        this.numberOfGuards = scenarioMap.getNumGuards();
        this.numberOfIntruders = scenarioMap.getNumIntruders();
        this.agentSpawnLocations = new Vector2D[numberOfGuards + numberOfIntruders];
        this.agents = new Agent[numberOfGuards + numberOfIntruders];
        this.fov = new FOV(scenarioMap.getGuardViewRange());
        this.timestep = scenarioMap.getTimestep();
        this.movementController = new MovementController(this);
        this.markerController = new MarkerController(this);
        this.endingExploration = new EndingExploration(scenarioMap);

        init();
    }

    private void init() {
        initializeAgents();

        Vector2D[] initialPositions = spawnAgents();
        ArrayList<Vector2D>[] visions = new ArrayList[numberOfGuards+numberOfIntruders];
        for (int i = 0; i < visions.length; i++) {
            visions[i] = calculateFOV(i, initialPositions[i]);
        }

        currentState = new State(initialPositions, visions, markerController.init(initialPositions));
        nextState = currentState.copyOf();
        updateProgress(); // Set the beginning "progress"
    }

    public void engine(){
        while (!endingExploration.isExplored()) {
            tick();
        }
        end();
    }

    public void tick() {
        for (int i = 0; i < agents.length; i++) {
            int movementTask = agents[i].tick(getTilesInVision(currentState.getVision(i), i),
                    convertRelativeCurrentPosToRelativeToSpawn(currentState.getVision(i), i),
                    markerController.getPheromoneMarkersDirections(i, currentState.getAgentPosition(i)));

            movementController.moveAgent(i, movementTask);
            nextState.setAgentVision(i, calculateFOV(i, currentState.getAgentPosition(i)));
        }
        markerController.tick();
        updateProgress();

        currentState = nextState;
        nextState = currentState.copyOf();
        time += timestep;
    }

    protected void initializeAgents() {}
    protected void updateProgress() {}

    protected ArrayList<Vector2D> calculateFOV(int agentIndex, Vector2D agentPosition) {
        return fov.calculate(agents[agentIndex].getView_angle(), agents[agentIndex].getView_range(), scenarioMap.createAreaMap(agentPosition, agents[agentIndex].getView_range()), agents[agentIndex].getOrientation()).getInVision();
    }

    private ArrayList<Tile> getTilesInVision(ArrayList<Vector2D> positions, int agentIndex) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Vector2D vector2D : positions) {
            Vector2D abs = convertRelativeCurrentPosToAbsolute(vector2D, agentIndex);
            tiles.add(scenarioMap.getTile(abs));
        }
        return tiles;
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
        Vector2D[] agentPositions = new Vector2D[numberOfGuards];

        for (int i = 0; i < numberOfGuards; i++) {
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

    public double getTimestep() {
        return timestep;
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
        return relPos.add(currentState.getAgentPosition(agentId));
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToAbsolute(List<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToAbsolute(vector2D, agentId));
        }
        return absPos;
    }

    public Vector2D convertRelativeCurrentPosToRelativeToSpawn(Vector2D relPos, int agentId) {
        return convertAbsoluteToRelativeSpawn(relPos.add(currentState.getAgentPosition(agentId)), agentId);
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToRelativeToSpawn (List<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToRelativeToSpawn(vector2D, agentId));
        }
        return absPos;
    }
}
