package gamelogic.controller.gamemodecontrollers;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.agent.brains.ExplorationBrain;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.Controller;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.maps.ScenarioMap;
import gui.gamescreen.AgentType;

public class ControllerExploration extends Controller {
    private final EndingExploration endingExploration;

    public ControllerExploration(ScenarioMap scenarioMap, EndingExploration endingCondition, TaskContainer taskContainer, int seed) {
        super(scenarioMap, endingCondition, taskContainer, seed);
        this.endingExploration = endingCondition;
    }

    @Override
    public void init() {
        super.init();
        updateProgress();
    }

    @Override
    protected void initializeAgents() {
        int[] orientations = {0, 90, 180, 270};

        for (int i = 0; i < numberOfGuards; i++) {
            agents[i] = new Agent(scenarioMap.getBaseSpeedGuard(), scenarioMap.getGuardViewAngle(),scenarioMap.getGuardViewRange(), orientations[rand.nextInt(orientations.length)], new ExplorationBrain(taskContainer));
        }
    }

    @Override
    protected void tickAgent(int agentIndex) {
        int movementTask = agents[agentIndex].tick(getVisions(agentIndex),
                markerController.getPheromoneMarkersDirection(agentIndex, currentState.getAgentPosition(agentIndex), AgentType.GUARD),
                null, null, null, null, null);

        movementController.moveAgent(agentIndex, movementTask);
        nextState.setAgentVision(agentIndex, calculateFOVAbsolute(agentIndex, nextState.getAgentPosition(agentIndex), nextState));
    }

    @Override
    protected void updateProgress() {
        for (int i = 0; i < numberOfGuards+numberOfIntruders; i++) {
            for (Vector2D vector : nextState.getVision(i)) {
                endingExploration.updateExplorationMap(vector);
            }
        }
    }

    @Override
    public void end() {
        int hours = (int) time / 3600;
        int minutes = ((int)time % 3600) / 60;
        double seconds = time % 60;
        if (threadPool != null) threadPool.shutdown();
        System.out.println("Everything is explored. It took " + hours + " hour(s) " + minutes + " minutes " + seconds + " seconds.");
    }
}
