package gamelogic.controller.gamemodecontrollers;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.maps.ScenarioMap;

public class ControllerSurveillanceRewards extends ControllerSurveillance {
    private int[] rewards;

    public ControllerSurveillanceRewards(ScenarioMap scenarioMap, EndingSurveillance endingCondition, TaskContainer taskContainer, int seed) {
        super(scenarioMap, endingCondition, taskContainer, seed);
    }

    @Override
    protected void tickAgent(int agentIndex) {
        // Check if guard is doing evasion
        if (agentIndex >= numberOfGuards && agents[agentIndex].getCurrentTask() == TaskContainer.TaskType.INTRUDER_EVASION) {
            super.tickAgent(agentIndex);
            // Check if agent finished evasion
            if (agents[agentIndex].getCurrentTask() != TaskContainer.TaskType.INTRUDER_EVASION) {
                rewards[agentIndex-numberOfGuards] += 15;
            }
        }
        else super.tickAgent(agentIndex);
    }

    @Override
    protected void tickAgents() {
        boolean[] intrudersAlive = new boolean[numberOfIntruders];
        for (int i = numberOfGuards; i < numberOfGuards+numberOfIntruders; i++) {
            if (agents[i] != null && agents[i].getCurrentTask() == TaskContainer.TaskType.INTRUDER_EVASION) intrudersAlive[i] = true;
        }
        super.tickAgents();
        for (int i = 0; i < numberOfIntruders; i++) {
            if (intrudersAlive[i]) { // Was alive before
                if (agents[i+numberOfGuards] == null) rewards[i] -= 10; // Died doing evasion
                else rewards[i] += 1; // Still alive doing evasion
            }
        }
    }

    public int getRewards() {
        int sum = 0;
        for (int i = 0; i < rewards.length; i++) {
            sum += rewards[i];
        }
        return sum;
    }
}
