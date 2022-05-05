package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.datacarriers.GuardYell;
import gamelogic.datacarriers.Sound;

import java.util.ArrayList;
import java.util.List;

public class SoundController {
    private final Controller controller;
    private final double footstepMaxHearingDistance;
    private final double yellMaxHearingDistance;
    private final double soundStandardDeviation;

    public SoundController(Controller controller) {
        this.controller = controller;
        this.footstepMaxHearingDistance = controller.scenarioMap.getFootstepMaxHearingDistance();
        this.yellMaxHearingDistance = controller.scenarioMap.getYellMaxHearingDistance();
        this.soundStandardDeviation = controller.scenarioMap.getSoundStandardDeviation();
    }

    public List<Sound> getSoundDirections(int agentIndex) {
        ArrayList<Sound> sounds = new ArrayList<>();
        for (int i = 0; i < controller.numberOfGuards+controller.numberOfIntruders; i++) {
            if (i != agentIndex) {
                // TODO Different noise for turning
                // TODO No noise when standing still
                double distance = controller.getCurrentState().getAgentPosition(agentIndex).dist(controller.getCurrentState().getAgentPosition(i));
                if (distance <= footstepMaxHearingDistance) {
                    double angle = controller.getCurrentState().getAgentPosition(agentIndex).getAngleBetweenVector(controller.getCurrentState().getAgentPosition(i));
                    double angleNormalDistributed = addNoiseToSound(angle);
                    sounds.add(new Sound(angleNormalDistributed >= 360 ? angleNormalDistributed-360 : angleNormalDistributed, (footstepMaxHearingDistance -distance) / footstepMaxHearingDistance));
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
            if (guardYell.agentIndex() != agentIndex ) {
                double distance = currentPos.dist(guardYell.origin());
                if (distance <= yellMaxHearingDistance) {
                    double angleWithNoise = addNoiseToSound(currentPos.getAngleBetweenVector(guardYell.origin()));
                    anglesOfGuardYell.add(new Sound(angleWithNoise >= 360 ? angleWithNoise - 360 : angleWithNoise, (yellMaxHearingDistance -distance) / yellMaxHearingDistance));
                }
            }
        }
        return anglesOfGuardYell;
    }

    public void guardYell(int agentIndex) {
        controller.nextState.addGuardYell(new GuardYell(controller.getCurrentState().getAgentPosition(agentIndex), agentIndex));
    }

    private double addNoiseToSound(double angle) {
        return controller.rand.nextGaussian()*soundStandardDeviation+angle; // SD = value from file, mean = angle found
    }
}
