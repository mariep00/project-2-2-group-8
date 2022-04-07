package gamelogic.controller;

import gamelogic.Vector2D;
import gamelogic.maps.ScenarioMap;

import java.util.ArrayList;

public class ControllerSurveillance extends Controller {
    public ControllerSurveillance(ScenarioMap scenarioMap) {
        super(scenarioMap);
    }

    @Override
    protected Vector2D[] spawnAgents() {
        Vector2D[] guardPositions = super.spawnAgents();

        Vector2D[] agentPositions = new Vector2D[NUMBER_OF_GUARDS+NUMBER_OF_INTRUDERS];
        ArrayList<Integer> indicesUsed = new ArrayList<>();
        ArrayList<Vector2D> spawnAreaIntruders = scenarioMap.getSpawnAreaIntruders();

        for (int i = 0; i < guardPositions.length; i++) {
            agentPositions[i] = guardPositions[i];
        }
        for (int i = NUMBER_OF_GUARDS; i < NUMBER_OF_INTRUDERS+NUMBER_OF_GUARDS; i++) {
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
}
