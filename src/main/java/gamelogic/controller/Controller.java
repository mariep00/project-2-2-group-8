package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingConditionInterface;
import gamelogic.datacarriers.Vision;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.ScenarioMap;

import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("unchecked")

// Abstract because you should not instantiate a Controller class, but either a ControllerExploration or ControllerSurveillance class
public abstract class Controller {
    protected static Random rand;

    public final TaskContainer taskContainer;
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

    protected final ThreadPoolExecutor threadPool;

    public Controller(ScenarioMap scenarioMap, EndingConditionInterface endingCondition, TaskContainer taskContainer, int seed) {
        this.taskContainer = taskContainer;
        this.scenarioMap = scenarioMap;
        this.numberOfGuards = scenarioMap.getNumGuards();
        this.numberOfIntruders = scenarioMap.getNumIntruders();
        this.agentSpawnLocations = new Vector2D[numberOfGuards + numberOfIntruders];
        this.agents = new Agent[numberOfGuards + numberOfIntruders];
        this.timestep = scenarioMap.getTimestep();
        this.movementController = new MovementController(this);
        this.markerController = new MarkerController(this);
        this.endingCondition = endingCondition;
        Controller.rand = new Random(seed);
    }

    public void init() {
        Vector2D[] initialPositions = spawnAgents();
        initializeAgents();
        
        currentState = new State(initialPositions, new ArrayList[numberOfGuards + numberOfIntruders], null, null);

        List<Vector2D>[] visions = new ArrayList[numberOfGuards + numberOfIntruders];
        for (int i = 0; i < visions.length; i++) {
            visions[i] = calculateFOVAbsolute(i, initialPositions[i], currentState);
            currentState.setAgentVision(i, visions[i]);
        }

        currentState = new State(initialPositions, visions, markerController.init(initialPositions),
                new VisionMemory[numberOfGuards + numberOfIntruders][numberOfGuards + numberOfIntruders]);

        nextState = currentState.copyOf();
    }

    public void engine() {
        while (!endingCondition.gameFinished()) {
            tick(false);
        }
        end();
    }

    public void tick() { tick(true); }
    public void tick(boolean checkEndingCondition) {
        if (checkEndingCondition) {
            if (!endingCondition.gameFinished()) {
                tickMethods();
            } else end();
        }
        else tickMethods();
    }

    public void tickMethods() {
        // First tick the agents, after that update other stuff
        tickAgents();
        updateAgentsSeen();
        markerController.tick();
        updateProgress();

        switchToNextState();
    }

    protected void tickAgents() {
        for (int i = 0; i < agents.length; i++) {
            if (agents[i] != null) {
                tickAgent(i);
            }
        }
    }

    protected void switchToNextState() {
        updateGui();
        currentState = nextState;
        nextState = currentState.copyOf();
        time += timestep;
    }

    public int getSteps(){
        return (int) (time/getTimestep());
    }

    public String getTotalTime(){
        int hours = (int) time / 3600;
        int minutes = ((int)time % 3600) / 60;
        double seconds = time % 60;
        return hours +" "+minutes+" "+seconds;
    }

    protected List<Vector2D> calculateFOV(int agentIndex, Vector2D agentPosition) {
        return VisionController.calculateVision(agents[agentIndex].getView_angle(), agents[agentIndex].getView_range(), scenarioMap.createAreaMap(agentPosition, agents[agentIndex].getView_range()), agents[agentIndex].getOrientation()).getInVision();
    }
    protected List<Vector2D> calculateFOVAbsolute(int agentIndex, Vector2D agentPosition, State state) {
        return convertRelativeCurrentPosToAbsolute(calculateFOV(agentIndex, agentPosition), agentIndex, state);
    }

    protected Vision[] getVisions(int agentIndex) {
        List<Vector2D> positionsInVision = currentState.getVision(agentIndex);
        Vision[] visions = new Vision[positionsInVision.size()];
        for (int i = 0; i < visions.length; i++) {
            Vector2D pos = positionsInVision.get(i);
            visions[i] = new Vision(scenarioMap.getTile(pos), convertAbsoluteToRelativeSpawn(pos, agentIndex));
        }
        return visions;
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

    public boolean isWallInBetween(Vector2D begin, Vector2D end) {
        Vector2D[] positions = VisionController.calculateLine(begin, end);
        for (Vector2D pos : positions) {
            if (scenarioMap.getTile(pos).isWall()) return true;
        }
        return false;
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

    public Vector2D convertAbsolutePosToRelativeToCurrentPos(Vector2D absPos, int agentId, State state) {
        return new Vector2D(absPos.x-state.getAgentPosition(agentId).x, absPos.y-state.getAgentPosition(agentId).y);
    }

    public static double addNoise(double initial, double std, boolean isAngle) {
        double temp = rand.nextGaussian()*std+initial; // SD = value from file, mean = initial value
        if (isAngle) {
            if (temp >= 360) return temp - 360;
            else if (temp < 0) return 360 - temp;
        }
        return temp;
    }

    protected void updateAgentsSeen() {}
    protected void tickAgent(int agentIndex) {}
    protected void updateGui() {}
    protected void initializeAgents() {}
    protected void updateProgress() {}
    public void end() {}

    public int getNumberOfGuards() { return numberOfGuards; }
    public int getNumberOfIntruders() { return numberOfIntruders; }
    public State getCurrentState() { return currentState; }
    public State getNextState() { return nextState; }
    public double getTimestep() { return timestep; }
    public double getTime() {return time;}
    public EndingConditionInterface getEndingCondition(){
        return endingCondition;
    }

    public ScenarioMap getScenarioMap() {
        return scenarioMap;
    }

    public Agent getAgent(int agentIndex) { return agents[agentIndex]; }

    private Callable<Void> toCallable(final Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }
}