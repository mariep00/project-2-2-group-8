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
    private static final boolean MULTITHREAD_CONTROLLER = false; // Change this to enable or disable multithreading in the controller. Currently, only ticking agents will be multithreaded.
    protected final Random rand = new Random();

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

    private final ThreadPoolExecutor threadPool;

    public Controller(ScenarioMap scenarioMap, EndingConditionInterface endingCondition, TaskContainer taskContainer) {
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
        if (MULTITHREAD_CONTROLLER) threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()/2, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        else threadPool = null;
    }

    public void init() {
        initializeAgents();
        Vector2D[] initialPositions = spawnAgents();
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

        List<Callable<Void>> taskList = new ArrayList<>();
        if (MULTITHREAD_CONTROLLER) taskList.add(toCallable(this::updateAgentsSeen));
        else updateAgentsSeen();
        if (MULTITHREAD_CONTROLLER) taskList.add(toCallable(markerController::tick));
        else markerController.tick();
        if (MULTITHREAD_CONTROLLER) taskList.add(toCallable(this::updateProgress));
        else updateProgress();

        if (MULTITHREAD_CONTROLLER) {
            try {
                threadPool.invokeAll(taskList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        switchToNextState();
    }

    protected void tickAgents() {
        List<Callable<Void>> taskList = new ArrayList<>();

        for (int i = 0; i < agents.length; i++) {
            if (agents[i] != null) {
                int finalI = i;
                if (MULTITHREAD_CONTROLLER) taskList.add(toCallable(() -> tickAgent(finalI)));
                else tickAgent(finalI);
            }
        }
        if (MULTITHREAD_CONTROLLER) {
            try {
                threadPool.invokeAll(taskList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        if (threadPool != null) threadPool.shutdown();
        System.out.println("Everything is explored. It took " + hours + " hour(s) " + minutes + " minutes " + seconds + " seconds.");
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

    public static double addNoise(double initial, double std) {
        Random random = new Random();
        return random.nextGaussian()*std+initial; // SD = value from file, mean = initial value
    }

    protected void updateAgentsSeen() {}
    protected void tickAgent(int agentIndex) {}
    protected void updateGui() {}
    protected void initializeAgents() {}
    protected void updateProgress() {}

    public int getNumberOfGuards() { return numberOfGuards; }
    public int getNumberOfIntruders() { return numberOfIntruders; }
    public State getCurrentState() { return currentState; }
    public State getNextState() { return nextState; }
    public double getTimestep() { return timestep; }
    public double getTime() {return time;}

    public Agent getAgent(int agentIndex) { return agents[agentIndex]; }

    private Callable<Void> toCallable(final Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }
}