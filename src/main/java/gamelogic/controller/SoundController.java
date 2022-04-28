package gamelogic.controller;

import datastructures.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class SoundController {
    private final Controller controller;
    private final int footstepMaxDistance = 8;
    private final int yellMaxDistance = 50;

    public SoundController(Controller controller) {
        this.controller = controller;
    }

    public List<Sound> getSoundDirections(int agentIndex) {
        ArrayList<Sound> sounds = new ArrayList<>();
        for (int i = 0; i < controller.numberOfGuards+controller.numberOfIntruders; i++) {
            if (i != agentIndex) {
                double distance = controller.getCurrentState().getAgentPosition(agentIndex).dist(controller.getCurrentState().getAgentPosition(i));
                if (distance <= footstepMaxDistance) {
                    double angle = controller.getCurrentState().getAgentPosition(agentIndex).getAngleBetweenVector(controller.getCurrentState().getAgentPosition(i));
                    double angleNormalDistributed = addNoiseToSound(angle);
                    sounds.add(new Sound(angleNormalDistributed >= 360 ? angleNormalDistributed-360 : angleNormalDistributed, distance / footstepMaxDistance));
                }
            }
        }
        return sounds;
    }

    public List<Sound> getGuardYellDirections(int agentIndex) {
        Vector2D currentPos = controller.getCurrentState().getAgentPosition(agentIndex);
        List<GuardYell> guardGuardYells = controller.getCurrentState().getGuardYells();
        ArrayList<Sound> anglesOfGuardYell = new ArrayList<>();
        for (GuardYell guardYell : guardGuardYells) {
            if (guardYell.agentIndex != agentIndex ) {
                double distance = currentPos.dist(guardYell.position);
                if (distance <= yellMaxDistance) {
                    double angleWithNoise = addNoiseToSound(currentPos.getAngleBetweenVector(guardYell.position));
                    anglesOfGuardYell.add(new Sound(angleWithNoise >= 360 ? angleWithNoise - 360 : angleWithNoise, distance / yellMaxDistance));
                }
            }
        }
        return anglesOfGuardYell;
    }

    public void guardYell(int agentIndex) {
        controller.nextState.addGuardYell(new GuardYell(controller.getCurrentState().getAgentPosition(agentIndex), agentIndex));
    }

    private double addNoiseToSound(double angle) {
        return controller.rand.nextGaussian()*10+angle; // SD = 10 (given in the manual), mean = angle found
    }
}
