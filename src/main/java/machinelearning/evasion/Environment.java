package machinelearning.evasion;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillanceRLEvasion;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gui.gamescreen.AgentType;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.Arrays;
import java.util.List;

public class Environment implements MDP<GameState, Integer, DiscreteSpace> {
    private final ControllerSurveillanceRLEvasion controller;
    private final DiscreteSpace actionSpace = new DiscreteSpace(4);
    private final boolean[] intruderPerformsEvasion;
    private int count = 0;

    private int agentIndex;

    public Environment(ControllerSurveillanceRLEvasion controller) {
        this.controller = controller;
        this.intruderPerformsEvasion = new boolean[controller.getNumberOfIntruders()];
        this.agentIndex = controller.getNumberOfGuards();
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
        System.out.println("Game count " + count);
        count++;
        controller.reset();
        return controller.buildStateObservation(agentIndex, null, null, null, null, -1, true);
    }

    @Override
    public void close() { }

    @Override
    public StepReply<GameState> step(Integer taskIndex) {
        int movementTask = NetworkUtil.taskIndexToMovementTask(taskIndex);
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

        int reward = 0;
        GameState observation;
        controller.getAgent(agentIndex).updateGraph(controller.getVisions(agentIndex)); // Update the intruders' graph

        double phermoneMarkersDirection = controller.markerController.getPheromoneMarkersDirection(agentIndex, controller.getCurrentState().getAgentPosition(agentIndex), AgentType.INTRUDER);
        List<Sound> sounds = controller.soundController.getSoundDirections(agentIndex);
        VisionMemory[] visionMemories = controller.getCurrentState().getAgentsSeen(agentIndex);
        TaskInterface intruderWantsToPerformTask = controller.getAgent(agentIndex).getTaskFromDecider(phermoneMarkersDirection, sounds,
                Arrays.copyOfRange(visionMemories, 0, controller.getNumberOfGuards()),
                Arrays.copyOfRange(visionMemories, controller.getNumberOfGuards(), controller.getNumberOfGuards() + controller.getNumberOfIntruders()),
                null);
        //System.out.println("Wants to perform task: " + intruderWantsToPerformTask.getType());
        // Check if the intruder wants to do evasion
        if (intruderWantsToPerformTask.getType() == TaskContainer.TaskType.INTRUDER_EVASION) {
            //System.out.println("Intruder performs evasion");
            //System.out.println("Intruder performs evasion " + count + " " + movementTask);
            //count++;
            intruderPerformsEvasion[agentIndex-controller.getNumberOfGuards()] = true;
            reward = controller.tickIntruder(agentIndex, movementTask);
            EvasionTaskBaseline evasionTaskBaseline = ((EvasionTaskBaseline) intruderWantsToPerformTask);
            observation = controller.buildStateObservation(agentIndex, evasionTaskBaseline.getSoundToEvadeFrom(), evasionTaskBaseline.getVisionToEvadeFrom(), visionMemories, sounds, phermoneMarkersDirection, false);
        }
        else {
            if (intruderPerformsEvasion[agentIndex-controller.getNumberOfGuards()]) {
                reward += 15;
                intruderPerformsEvasion[agentIndex-controller.getNumberOfGuards()] = false;
            }
            int tempMovementTask = controller.getAgent(agentIndex).makeDecision(phermoneMarkersDirection, sounds,
                    Arrays.copyOfRange(visionMemories, 0, controller.getNumberOfGuards()),
                    Arrays.copyOfRange(visionMemories, controller.getNumberOfGuards(), controller.getNumberOfGuards() + controller.getNumberOfIntruders()),
                    null, null);

            controller.movementController.moveAgent(agentIndex, tempMovementTask);
            controller.getNextState().setAgentVision(agentIndex, controller.calculateFOVAbsolute(agentIndex, controller.getNextState().getAgentPosition(agentIndex), controller.getNextState()));
            observation = controller.buildStateObservation(agentIndex, null, null, null, null, -1, true);
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
        System.out.println("RESET");
        controller.reset();
        return new Environment(controller);
    }
}
