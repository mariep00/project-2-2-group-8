package gamelogic.controller.gamemodecontrollers;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.agent.brains.GuardBrain;
import gamelogic.agent.brains.IntruderBrain;
import gamelogic.controller.Controller;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.maps.ScenarioMap;

import java.util.ArrayList;

public class ControllerSurveillance extends Controller {
    private final EndingSurveillance endingSurveillance;

    public ControllerSurveillance(ScenarioMap scenarioMap, EndingSurveillance endingCondition) {
        super(scenarioMap, endingCondition);
        this.endingSurveillance = endingCondition;
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
                if (!indicesUsed.contains(rand)) {
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

        for (int i = 0; i < numberOfIntruders; i++) {
            agents[i] = new Agent(scenarioMap.getBaseSpeedIntruder(), scenarioMap.getSprintSpeedIntruder(), scenarioMap.getIntruderViewAngle(), scenarioMap.getIntruderViewRange(), orientations[rand.nextInt(orientations.length)], new IntruderBrain());
        }
        for (int i = 0; i < numberOfGuards; i++) {
            agents[i] = new Agent(scenarioMap.getBaseSpeedGuard(), 0.0, scenarioMap.getGuardViewAngle(),scenarioMap.getGuardViewRange(), orientations[rand.nextInt(orientations.length)], new GuardBrain());
        }
    }
}
