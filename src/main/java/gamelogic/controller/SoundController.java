package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.datacarriers.GuardYell;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.SoundOrigin;
import gamelogic.datacarriers.SoundType;

import java.util.ArrayList;
import java.util.List;

public class SoundController {
    private final Controller controller;
    private final double footstepMaxHearingDistance;
    private final double rotatingMaxHearingDistance;
    private final double yellMaxHearingDistance;
    private final double soundStandardDeviation;

    public SoundController(Controller controller) {
        this.controller = controller;
        this.footstepMaxHearingDistance = controller.scenarioMap.getFootstepMaxHearingDistance();
        this.rotatingMaxHearingDistance = controller.scenarioMap.getRotatingMaxHearingDistance();
        this.yellMaxHearingDistance = controller.scenarioMap.getYellMaxHearingDistance();
        this.soundStandardDeviation = controller.scenarioMap.getSoundStandardDeviation();
    }

    public List<Sound> getSoundDirections(int agentIndex) {
        ArrayList<Sound> sounds = new ArrayList<>();
        for (SoundOrigin soundOrigin : controller.currentState.getSoundOrigins()) {
            if (soundOrigin.agentIndex() != agentIndex) {
                double distance = controller.getCurrentState().getAgentPosition(agentIndex).dist(soundOrigin.origin());
                double threshold;
                if (soundOrigin.soundType() == SoundType.WALKING) threshold = footstepMaxHearingDistance;
                else threshold = rotatingMaxHearingDistance;

                if (distance <= threshold && controller.isWallInBetween(controller.currentState.getAgentPosition(agentIndex), soundOrigin.origin())) {
                    double angle = controller.getCurrentState().getAgentPosition(agentIndex).getAngleBetweenVector(soundOrigin.origin());
                    double angleNormalDistributed = addNoiseToSound(angle);
                    double maxThreshold = Math.max(footstepMaxHearingDistance, rotatingMaxHearingDistance); // Divide by maximum to have a normalised loudness
                    sounds.add(new Sound(angleNormalDistributed >= 360 ? angleNormalDistributed - 360 : angleNormalDistributed, (maxThreshold - distance) / maxThreshold));
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
                    anglesOfGuardYell.add(new Sound(angleWithNoise >= 360 ? angleWithNoise - 360 : angleWithNoise, (yellMaxHearingDistance-distance) / yellMaxHearingDistance));
                }
            }
        }
        return anglesOfGuardYell;
    }

    public void generateWalkSound(int agentIndex) {
        controller.nextState.addSoundOrigin(new SoundOrigin(controller.currentState.getAgentPosition(agentIndex), SoundType.WALKING, agentIndex));
    }
    public void generateTurnSound(int agentIndex) {
        controller.nextState.addSoundOrigin(new SoundOrigin(controller.currentState.getAgentPosition(agentIndex), SoundType.ROTATING, agentIndex));
    }
    public void generateGuardYell(int agentIndex) {
        controller.nextState.addGuardYell(new GuardYell(controller.getCurrentState().getAgentPosition(agentIndex), agentIndex));
    }

    private double addNoiseToSound(double angle) {
        return controller.rand.nextGaussian()*soundStandardDeviation+angle; // SD = value from file, mean = angle found
    }
}
