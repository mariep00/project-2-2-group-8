package machinelearning.evasion;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.controller.State;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillanceRLEvasion;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.Arrays;

public class Environment implements MDP<GameState, Integer, DiscreteSpace> {
    private final ControllerSurveillanceRLEvasion controller;
    private final DiscreteSpace actionSpace = new DiscreteSpace(4);

    private int agentIndex;

    public Environment(ControllerSurveillanceRLEvasion controller) {
        this.controller = controller;
    }

    @Override
    public ObservationSpace<State> getObservationSpace() {
        return new GameObservationSpace();
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public State reset() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<GameState> step(Integer movementTask) {
        if (agentIndex == controller.getNumberOfGuards()) {
            for (int i = 0; i < controller.getNumberOfGuards(); i++) {
                controller.tickAgent(i);
            }
        }
        int reward = 0;
        if (controller.getAgent(agentIndex).getCurrentTask() == TaskContainer.TaskType.INTRUDER_EVASION) {
            reward = controller.tickIntruder(agentIndex, movementTask);
        }
        else {
            int tempMovementTask = controller.getAgent(agentIndex).tick(controller.getVisions(agentIndex),
                    controller.markerController.getPheromoneMarkersDirection(agentIndex, controller.getCurrentState().getAgentPosition(agentIndex)),
                    controller.soundController.getSoundDirections(agentIndex), Arrays.copyOfRange(controller.getCurrentState().getAgentsSeen(agentIndex), 0, controller.getNumberOfGuards()),
                    Arrays.copyOfRange(controller.getCurrentState().getAgentsSeen(agentIndex), controller.getNumberOfGuards(), controller.getNumberOfGuards() + controller.getNumberOfIntruders()),
                    controller.soundController.getGuardYellDirections(agentIndex));

            if (controller.getAgent(agentIndex).getCurrentTask() == TaskContainer.TaskType.INTRUDER_EVASION) {
                tempMovementTask = movementTask;
            }

            controller.movementController.moveAgent(agentIndex, tempMovementTask);
            controller.getNextState().setAgentVision(agentIndex, controller.calculateFOVAbsolute(agentIndex, controller.getNextState().getAgentPosition(agentIndex), controller.getNextState()));
        }

        agentIndex++;
        if (agentIndex >= controller.getNumberOfGuards()+ controller.getNumberOfIntruders()) {
            agentIndex = controller.getNumberOfGuards();
            controller.switchToNextState();
        }

        GameState observation;
        if (controller.getAgent(agentIndex).getCurrentTask() == TaskContainer.TaskType.INTRUDER_EVASION) {
            observation = controller.buildStateObservation(agentIndex, (Evascontroller.getAgent(agentIndex).getCurrentTask());
        }
        return new StepReply<>(observation, reward, controller.getEndingCondition().gameFinished(), "IntruderEvasionRL");
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public MDP<State, Integer, DiscreteSpace> newInstance() {
        return null;
    }
}
