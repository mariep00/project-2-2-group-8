package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.controller.endingconditions.EndingConditionInterface;
import gamelogic.maps.ScenarioMap;
import gamelogic.maps.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Abstract because you should not instantiate a Controller class, but either a ControllerExploration or ControllerSurveillance class
public abstract class Controller {
    protected final Random rand = new Random();

    public final MovementController movementController;
    public final MarkerController markerController;
    protected final ScenarioMap scenarioMap;
    protected final EndingConditionInterface endingCondition;
    protected final int numberOfGuards;
    protected final int numberOfIntruders;
    protected final Agent[] agents;
    protected final Vector2D[] agentSpawnLocations;

    protected State currentState;
    protected State nextState;

    protected double timestep;
    public double time;

    public Controller(ScenarioMap scenarioMap, EndingConditionInterface endingCondition) {
        this.scenarioMap = scenarioMap;
        this.numberOfGuards = scenarioMap.getNumGuards();
        this.numberOfIntruders = scenarioMap.getNumIntruders();
        this.agentSpawnLocations = new Vector2D[numberOfGuards + numberOfIntruders];
        this.agents = new Agent[numberOfGuards + numberOfIntruders];
        this.timestep = scenarioMap.getTimestep();
        this.movementController = new MovementController(this);
        this.markerController = new MarkerController(this);
        this.endingCondition = endingCondition;
    }

    public void init() {
        initializeAgents();

        Vector2D[] initialPositions = spawnAgents();
        currentState = new State(initialPositions, null, null);

        ArrayList<Vector2D>[] visions = new ArrayList[numberOfGuards + numberOfIntruders];
        for (int i = 0; i < visions.length; i++) {
            visions[i] = calculateFOVAbsolute(i, initialPositions[i], currentState);
        }

        currentState = new State(initialPositions, visions, markerController.init(initialPositions));
        nextState = currentState.copyOf();
    }

    public void engine() {
        while (!endingCondition.gameFinished()) {
            tick();
        }
        end();
    }

    public void tick() {
        for (int i = 0; i < agents.length; i++) {
            int movementTask = agents[i].tick(getTilesInVision(currentState.getVision(i)),
                    convertAbsoluteToRelativeSpawn(currentState.getVision(i), i),
                    markerController.getPheromoneMarkersDirection(i, currentState.getAgentPosition(i)));

            movementController.moveAgent(i, movementTask);
            nextState.setAgentVision(i, calculateFOVAbsolute(i, nextState.getAgentPosition(i), nextState));
        }
        markerController.tick();
        updateProgress();
        switchToNextState();
    }

    protected void switchToNextState() {
        updateGui();
        currentState = nextState;
        nextState = currentState.copyOf();
        time += timestep;
    }

    protected void end() {
        int hours = (int) time / 3600;
        int minutes = ((int)time % 3600) / 60;
        double seconds = time % 60;
        System.out.println("Everything is explored. It took " + hours + " hour(s) " + minutes + " minutes " + seconds + " seconds.");
    }

    protected ArrayList<Vector2D> calculateFOV(int agentIndex, Vector2D agentPosition) {
        return VisionController.calculateVision(agents[agentIndex].getView_angle(), agents[agentIndex].getView_range(), scenarioMap.createAreaMap(agentPosition, agents[agentIndex].getView_range()), agents[agentIndex].getOrientation()).getInVision();
    }
    protected ArrayList<Vector2D> calculateFOVAbsolute(int agentIndex, Vector2D agentPosition, State state) {
        return convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPosition), agentIndex, state);
    }

    private ArrayList<Tile> getTilesInVision(List<Vector2D> vision) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Vector2D pos : vision) {
            tiles.add(scenarioMap.getTile(pos));
        }
        return tiles;
    }

    protected Vector2D[] spawnAgents() {
        ArrayList<Integer> indicesUsed = new ArrayList<>();
        ArrayList<Vector2D> spawnAreaGuards = scenarioMap.getSpawnAreaGuards();
        Vector2D[] agentPositions = new Vector2D[numberOfGuards];

        for (int i = 0; i < numberOfGuards; i++) {
            while (true) {
                int randNumber = rand.nextInt(spawnAreaGuards.size());
                if (!indicesUsed.contains(randNumber)) {
                    indicesUsed.add(randNumber);
                    agentSpawnLocations[i] = spawnAreaGuards.get(randNumber);
                    agentPositions[i] = agentSpawnLocations[i];
                    break;
                }
            }
        }

        return agentPositions;
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
    public ArrayList<Vector2D> convertAbsoluteToRelativeSpawn(List<Vector2D> absPos, int agentId) {
        ArrayList<Vector2D> relPos = new ArrayList<>();
        for (Vector2D vector2D : absPos) {
            relPos.add(convertAbsoluteToRelativeSpawn(vector2D, agentId));
        }
        return relPos;
    }

    public Vector2D convertRelativeCurrentPosToAbsolute(Vector2D relPos, int agentId, State state) {
        return relPos.add(state.getAgentPosition(agentId));
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToAbsolute(List<Vector2D> relPos, int agentId, State state) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToAbsolute(vector2D, agentId, state));
        }
        return absPos;
    }

    public Vector2D convertRelativeCurrentPosToRelativeToSpawn(Vector2D relPos, int agentId, State state) {
        return convertAbsoluteToRelativeSpawn(relPos.add(state.getAgentPosition(agentId)), agentId);
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToRelativeToSpawn (List<Vector2D> relPos, int agentId, State state) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToRelativeToSpawn(vector2D, agentId, state));
        }
        return absPos;
    }

    protected void updateGui() {}
    protected void initializeAgents() {}
    protected void updateProgress() {}

    public int getNumberOfGuards() { return numberOfGuards; }
    public int getNumberOfIntruders() { return numberOfIntruders; }
    public State getCurrentState() { return currentState; }
    public State getNextState() { return nextState; }
    public double getTimestep() {
        return timestep;
    }
    public Agent getAgent(int agentIndex) { return agents[agentIndex]; }
}
