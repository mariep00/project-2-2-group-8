package gamelogic.controller;

import datastructures.Vector2D;

import java.util.ArrayList;
import java.util.List;

// TODO Might want to add sound loudness
public class SoundController {
    private final Controller controller;
    private final int footstepMaxDistance = 8;
    private final int yellMaxDistance = 50;

    public SoundController(Controller controller) {
        this.controller = controller;
    }

    public List<Double> getSoundDirections(int agentIndex) {
        ArrayList<Double> soundAngles = new ArrayList<>();
        for (int i = 0; i < controller.numberOfGuards+controller.numberOfIntruders; i++) {
            if (i != agentIndex) {
                if (controller.getCurrentState().getAgentPosition(agentIndex).dist(controller.getCurrentState().getAgentPosition(i)) <= footstepMaxDistance) {
                    double angle = controller.getCurrentState().getAgentPosition(agentIndex).getAngleBetweenVector(controller.getCurrentState().getAgentPosition(i));
                    double angleNormalDistributed = addNoiseToSound(angle);
                    soundAngles.add(angleNormalDistributed >= 360 ? angleNormalDistributed-360 : angleNormalDistributed);
                }
            }
        }
        return soundAngles;
    }

    public List<Double> getGuardYellDirections(int agentIndex) {
        Vector2D currentPos = controller.getCurrentState().getAgentPosition(agentIndex);
        List<Yell> guardYells = controller.getCurrentState().getGuardYells();
        ArrayList<Double> anglesOfGuardYell = new ArrayList<>();
        for (Yell yell : guardYells) {
            if (yell.agentIndex != agentIndex && currentPos.dist(yell.position) <= yellMaxDistance) {
                double angleWithNoise = addNoiseToSound(currentPos.getAngleBetweenVector(yell.position));
                anglesOfGuardYell.add(angleWithNoise >= 360 ? angleWithNoise - 360 : angleWithNoise);
            }
        }
        return anglesOfGuardYell;
    }

    public void guardYell(int agentIndex) {
        controller.nextState.addGuardYell(new Yell(controller.getCurrentState().getAgentPosition(agentIndex), agentIndex));
    }

    private double addNoiseToSound(double angle) {
        return controller.rand.nextGaussian()*10+angle; // SD = 10 (given in the manual), mean = angle found
    }
}
