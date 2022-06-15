package machinelearning.evasion;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillanceRLEvasion;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.Arrays;

public class Environment implements MDP<GameState, Integer, DiscreteSpace> {
    private final ControllerSurveillanceRLEvasion controller;
    private final DiscreteSpace actionSpace = new DiscreteSpace(4);
    private final boolean[] intruderPerformsEvasion;

    private int agentIndex;

    public Environment(ControllerSurveillanceRLEvasion controller) {
        this.controller = controller;
        this.intruderPerformsEvasion = new boolean[controller.getNumberOfIntruders()];
    }

    @Override
    public ObservationSpace<GameState> getObservationSpace() {
        return new GameObservationSpace();
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public GameState reset() {
        return controller.buildStateObservation(agentIndex, null, null, true);
    }

    @Override
    public void close() { }

    @Override
    public StepReply<GameState> step(Integer movementTask) {
        if (agentIndex == controller.getNumberOfGuards()) {
            for (int i = 0; i < controller.getNumberOfGuards(); i++) {
                controller.tickAgent(i);
            }
        }
        while (controller.getAgent(agentIndex) == null) {
            agentIndex++;
            if (agentIndex >= controller.getNumberOfGuards() + controller.getNumberOfIntruders()) {
                agentIndex = controller.getNumberOfGuards();
            }
        }
        System.out.println("Is this really the task, or is it the index? " + movementTask);
        int reward = 0;
        GameState observation;
        controller.getAgent(agentIndex).updateGraph(controller.getVisions(agentIndex)); // Update the intruders' graph

        TaskInterface intruderWantsToPerformTask = controller.getAgent(agentIndex).getTaskFromDecider(controller.markerController.getPheromoneMarkersDirection(agentIndex, controller.getCurrentState().getAgentPosition(agentIndex)),
                controller.soundController.getSoundDirections(agentIndex), Arrays.copyOfRange(controller.getCurrentState().getAgentsSeen(agentIndex), 0, controller.getNumberOfGuards()),
                Arrays.copyOfRange(controller.getCurrentState().getAgentsSeen(agentIndex), controller.getNumberOfGuards(), controller.getNumberOfGuards() + controller.getNumberOfIntruders()),
                controller.soundController.getGuardYellDirections(agentIndex));

        // Check if the intruder wants to do evasion
        if (intruderWantsToPerformTask.getType() == TaskContainer.TaskType.INTRUDER_EVASION) {
            intruderPerformsEvasion[agentIndex-controller.getNumberOfGuards()] = true;
            reward = controller.tickIntruder(agentIndex, movementTask);
            EvasionTaskBaseline evasionTaskBaseline = ((EvasionTaskBaseline) intruderWantsToPerformTask);
            observation = controller.buildStateObservation(agentIndex, evasionTaskBaseline.getSoundToEvadeFrom(), evasionTaskBaseline.getVisionToEvadeFrom(), false);
        }
        else {
            if (intruderPerformsEvasion[agentIndex-controller.getNumberOfGuards()]) {
                reward += 15;
                intruderPerformsEvasion[agentIndex-controller.getNumberOfGuards()] = false;
            }
            int tempMovementTask = controller.getAgent(agentIndex).makeDecision(controller.markerController.getPheromoneMarkersDirection(agentIndex, controller.getCurrentState().getAgentPosition(agentIndex)),
                    controller.soundController.getSoundDirections(agentIndex), Arrays.copyOfRange(controller.getCurrentState().getAgentsSeen(agentIndex), 0, controller.getNumberOfGuards()),
                    Arrays.copyOfRange(controller.getCurrentState().getAgentsSeen(agentIndex), controller.getNumberOfGuards(), controller.getNumberOfGuards() + controller.getNumberOfIntruders()),
                    controller.soundController.getGuardYellDirections(agentIndex));

            controller.movementController.moveAgent(agentIndex, tempMovementTask);
            controller.getNextState().setAgentVision(agentIndex, controller.calculateFOVAbsolute(agentIndex, controller.getNextState().getAgentPosition(agentIndex), controller.getNextState()));
            observation = controller.buildStateObservation(agentIndex, null, null, true);
        }

        agentIndex++;
        if (agentIndex >= controller.getNumberOfGuards() + controller.getNumberOfIntruders()) {
            agentIndex = controller.getNumberOfGuards();

            controller.updateAgentsSeen();
            controller.markerController.tick();
            controller.updateProgress();
            controller.switchToNextState();
        }

        return new StepReply<>(observation, reward, controller.getEndingCondition().gameFinished(), "IntruderEvasionRL");
    }

    @Override
    public boolean isDone() {
        return controller.getEndingCondition().gameFinished();
    }

    @Override
    public MDP<GameState, Integer, DiscreteSpace> newInstance() {
        controller.reset();
        return new Environment(controller);
    }
}
