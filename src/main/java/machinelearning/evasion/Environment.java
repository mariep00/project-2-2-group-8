package machinelearning.evasion;

import gamelogic.controller.State;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillanceRewards;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class Environment implements MDP<State, Integer, DiscreteSpace> {
    private final ControllerSurveillanceRewards controllerSurveillanceRewards;

    public Environment(ControllerSurveillanceRewards controllerSurveillanceRewards) {
        this.controllerSurveillanceRewards = controllerSurveillanceRewards;
    }

    @Override
    public ObservationSpace<State> getObservationSpace() {
        return null;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return null;
    }

    @Override
    public State reset() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<State> step(Integer integer) {
        return null;
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
