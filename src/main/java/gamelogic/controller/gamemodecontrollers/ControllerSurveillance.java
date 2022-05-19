package gamelogic.controller.gamemodecontrollers;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.agent.brains.GuardBrain;
import gamelogic.agent.brains.IntruderBrain;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.Controller;
import gamelogic.controller.SoundController;
import gamelogic.controller.State;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.ScenarioMap;
import gamelogic.maps.Tile;
import gamelogic.maps.Tile.Type;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControllerSurveillance extends Controller {
    private final EndingSurveillance endingSurveillance;
    private final ExplorationGraph mapGraph;
    public final SoundController soundController;
    private final double[] yellCooldowns;
    private final double yellCooldown = 1;

    public ControllerSurveillance(ScenarioMap scenarioMap, EndingSurveillance endingCondition, TaskContainer taskContainer, int seed) {
        super(scenarioMap, endingCondition, taskContainer, seed);
        this.endingSurveillance = endingCondition;
        this.mapGraph = new ExplorationGraph();
        this.soundController = new SoundController(this);
        this.yellCooldowns = new double[scenarioMap.getNumGuards()];
        Arrays.fill(this.yellCooldowns, yellCooldown);
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < numberOfGuards+numberOfIntruders; i++) {
            currentState.setAgentsSeen(i, updateAgentVisionMemory(i, currentState));
            nextState.setAgentsSeen(i, updateAgentVisionMemory(i, currentState));
        }
        createGraph();
    }

    @Override
    protected Vector2D[] spawnAgents() {
        Vector2D[] guardPositions = super.spawnAgents();

        Vector2D[] agentPositions = new Vector2D[numberOfGuards + numberOfIntruders];
        ArrayList<Integer> indicesUsed = new ArrayList<>();
        ArrayList<Vector2D> spawnAreaIntruders = scenarioMap.getSpawnAreaIntruders();

        for (int i = 0; i < guardPositions.length; i++) {
            agentPositions[i] = guardPositions[i];
        }
        for (int i = numberOfGuards; i < numberOfIntruders + numberOfGuards; i++) {
            while (true) {
                int randNumber = rand.nextInt(spawnAreaIntruders.size());
                if (!indicesUsed.contains(randNumber)) {
                    indicesUsed.add(randNumber);
                    agentSpawnLocations[i] = spawnAreaIntruders.get(randNumber);
                    agentPositions[i] = agentSpawnLocations[i];
                    break;
                }
            }
        }
        return agentPositions;
    }

    @Override
    protected void initializeAgents() {
        int[] orientations = {0, 90, 180, 270};

        for (int i = 0; i < numberOfGuards; i++) {
            agents[i] = new Agent(scenarioMap.getBaseSpeedGuard(), scenarioMap.getGuardViewAngle(),scenarioMap.getGuardViewRange(), orientations[rand.nextInt(orientations.length)], new GuardBrain(taskContainer));
        }
        for (int i = numberOfGuards; i < numberOfGuards+numberOfIntruders; i++) {
            agents[i] = new Agent(scenarioMap.getBaseSpeedIntruder(), scenarioMap.getIntruderViewAngle(), scenarioMap.getIntruderViewRange(), orientations[rand.nextInt(orientations.length)], new IntruderBrain(taskContainer, getTargetAngle(i)));
        }
    }

    @Override
    protected void tickAgents() {
        super.tickAgents();
        for (int i = 0; i < numberOfGuards; i++) {
            for (int j = numberOfGuards; j < numberOfGuards+numberOfIntruders; j++) {
                if (agents[j] != null) {
                    if (nextState.getAgentPosition(i).dist(nextState.getAgentPosition(j)) <= Math.sqrt(2)) {
                        removeAgent(j);
                    }
                }
            }
        }
    }

    @Override
    protected void tickAgent(int agentIndex) {
        int movementTask = agents[agentIndex].tick(getVisions(agentIndex),
                markerController.getPheromoneMarkersDirection(agentIndex, currentState.getAgentPosition(agentIndex)),
                soundController.getSoundDirections(agentIndex), Arrays.copyOfRange(currentState.getAgentsSeen(agentIndex), 0, numberOfGuards),
                Arrays.copyOfRange(currentState.getAgentsSeen(agentIndex), numberOfGuards, numberOfGuards+numberOfIntruders),
                soundController.getGuardYellDirections(agentIndex));

        movementController.moveAgent(agentIndex, movementTask);
        nextState.setAgentVision(agentIndex, calculateFOVAbsolute(agentIndex, nextState.getAgentPosition(agentIndex), nextState));
    }

    @Override
    protected void updateProgress() {
        endingSurveillance.updateState(this);
    }

    @Override
    protected void updateAgentsSeen() {
        for (int i = 0; i < agents.length; i++) {
            if (agents[i] != null) {
                nextState.setAgentsSeen(i, updateAgentVisionMemory(i, nextState));
            }
        }
    }

    @Override
    public void end() {
        System.out.println("----- SURVEILLANCE ENDED, IT TOOK " + time  + " SECONDS -----");
    }

    private VisionMemory[] updateAgentVisionMemory(int agentIndex, State state) {
        VisionMemory[] otherAgentsSeen;
        if (state.getAgentsSeen(agentIndex) != null) otherAgentsSeen = state.getAgentsSeen(agentIndex);
        else otherAgentsSeen = new VisionMemory[numberOfGuards + numberOfIntruders]; // This would only happen when called from init()

        for (int i=0; i < otherAgentsSeen.length; i++) {
            boolean newlySeenIntruder = false;
            if (agents[i] != null) {
                if (i != agentIndex) {
                    for (Vector2D pos : state.getVision(agentIndex)) {
                        if (pos.equals(state.getAgentPosition(i))) {
                            otherAgentsSeen[i] = new VisionMemory(convertAbsolutePosToRelativeToCurrentPos(pos, agentIndex, state), 0, agents[i].getOrientation());
                            // Check if agent we're updating is a guard and agent we're seeing is an intruder, then yell
                            if (otherAgentsSeen[i].position().equals(new Vector2D(0, 0))) {
                               try {
                                   throw new Exception("Agent sees other agent at own position!");
                               } catch (Exception e) {
                                   e.printStackTrace();
                                   System.exit(1);
                               }
                            }
                            if (agentIndex < numberOfGuards && i >= numberOfGuards && isZero(yellCooldowns[agentIndex])) {
                                soundController.generateGuardYell(agentIndex);
                                yellCooldowns[agentIndex] = yellCooldown;
                            }
                            newlySeenIntruder = true;
                            break;
                        }
                    }
                }
            }
            // When reaching this the agent is not in vision
            if (otherAgentsSeen[i] != null && !newlySeenIntruder) {
                // Position is updated s.t. it stays relative to the current position of the agent
                // Seconds ago is incremented with the timestep
                otherAgentsSeen[i] = new VisionMemory(otherAgentsSeen[i].position().subtract((nextState.getAgentPosition(agentIndex).subtract(currentState.getAgentPosition(agentIndex)))),
                        otherAgentsSeen[i].secondsAgo() + timestep, otherAgentsSeen[i].orientation());
            }
        }
        if (agentIndex < numberOfGuards && !isZero(yellCooldowns[agentIndex])) {
            yellCooldowns[agentIndex] -= timestep;
        }
        return otherAgentsSeen;
    }

    private boolean isZero(double value) {
        return value >= -10e-12 && value <= 10e-12;
    }

    private void createGraph() {
        Tile[][] map = scenarioMap.getMapGrid();
        List<Vector2D> walls = new ArrayList<Vector2D>();
        for(int y=0; y<map.length; y++) {
            for(int x=0; x<map[0].length; x++) {
                if (map[y][x].getType() == Type.WALL) {
                    walls.add(new Vector2D(x, y));
                } else {
                    mapGraph.createNode(new Vector2D(x, y), map[y][x]);
                }
            }
        }
        for(Vector2D vector : walls) {
            Vector2D[] neighbours = vector.getNeighbours();
            for (int j=0; j<neighbours.length; j++) {
                mapGraph.addWall(neighbours[j], j);
            }
        }
    }

    protected void removeAgent(int agentIndex) {
        agents[agentIndex] = null;
        nextState.setAgentVision(agentIndex, null);
        nextState.setAgentPosition(agentIndex, null);
    }

    private double getTargetAngle(int agentIndex) {
        Vector2D targetArea = scenarioMap.getTargetArea().get(0);
        double angle = agentSpawnLocations[agentIndex].getAngleBetweenVector(targetArea);
        return addNoise(angle, 5);
    }

    public int getWhoWon() {
        boolean guardsWin = endingSurveillance.getWhoWon();
        if (guardsWin) {
            return 0;
        } else return 1;
    }

    public ExplorationGraph getMapGraph() { return mapGraph; }
}
