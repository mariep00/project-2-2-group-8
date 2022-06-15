package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.datacarriers.GuardYell;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.SoundOrigin;
import gamelogic.datacarriers.SoundType;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SoundController {
    private final Controller controller;
    private final ExplorationGraph mapGraph;
    private final int footstepMaxHearingDistance;
    private final int rotatingMaxHearingDistance;
    private final int yellMaxHearingDistance;
    private final double soundStandardDeviation;

    public SoundController(Controller controller) {
        this.controller = controller;
        this.footstepMaxHearingDistance = controller.scenarioMap.getFootstepMaxHearingDistance();
        this.rotatingMaxHearingDistance = controller.scenarioMap.getRotatingMaxHearingDistance();
        this.yellMaxHearingDistance = controller.scenarioMap.getYellMaxHearingDistance();
        this.soundStandardDeviation = controller.scenarioMap.getSoundStandardDeviation();
        this.mapGraph = ((ControllerSurveillance) controller).getMapGraph();
    }

    public List<Sound> getSoundDirections(int agentIndex) {
        ArrayList<Sound> sounds = new ArrayList<>();
        for (SoundOrigin soundOrigin : controller.currentState.getSoundOrigins()) {
            if (soundOrigin.agentIndex() != agentIndex) {
                int threshold;
                if (soundOrigin.soundType() == SoundType.WALKING) threshold = footstepMaxHearingDistance;
                else threshold = rotatingMaxHearingDistance;

                double distance = getDistanceToSound(controller.getCurrentState().getAgentPosition(agentIndex), soundOrigin.origin(), threshold);
                if (distance != -1) {
                    double angle = controller.getCurrentState().getAgentPosition(agentIndex).getAngleBetweenVector(soundOrigin.origin());
                    double angleNormalDistributed = Controller.addNoise(angle, soundStandardDeviation, true);
                    double maxThreshold = Math.max(footstepMaxHearingDistance, rotatingMaxHearingDistance); // Divide by maximum to have a normalised loudness
                    sounds.add(new Sound(angleNormalDistributed >= 360 ? angleNormalDistributed - 360 : angleNormalDistributed, ((float) maxThreshold - distance) / maxThreshold));

                    //SoundDecidingDataGenerator.generateData(sounds.get(sounds.size()-1), controller.getCurrentState().getAgentsSeen(agentIndex), soundOrigin.agentIndex());
                }
            }
        }
        return sounds;
    }

    public List<Sound> getGuardYellDirections(int agentIndex) {
        Vector2D currentPos = controller.getCurrentState().getAgentPosition(agentIndex);
        List<GuardYell> guardYells = controller.getCurrentState().getGuardYells();
        ArrayList<Sound> anglesOfGuardYell = new ArrayList<>();

        for (GuardYell guardYell : guardYells) {
            if (guardYell.agentIndex() != agentIndex ) {
                double distance = getDistanceToSound(controller.getCurrentState().getAgentPosition(agentIndex), guardYell.origin(), yellMaxHearingDistance);
                if (distance != -1) {
                    double angleWithNoise = Controller.addNoise(currentPos.getAngleBetweenVector(guardYell.origin()), soundStandardDeviation, true);
                    anglesOfGuardYell.add(new Sound(angleWithNoise >= 360 ? angleWithNoise - 360 : angleWithNoise, ((float) yellMaxHearingDistance-distance) / yellMaxHearingDistance));
                }
            }
        }
        return anglesOfGuardYell;
    }

    private double getDistanceToSound(Vector2D origin, Vector2D currentPosition, int threshold) {
        double distance = -1;
        if (origin.dist(currentPosition) <= threshold) {
            if (controller.isWallInBetween(currentPosition, origin)) {
                LinkedList<Vector2D> path = AStar.calculate(mapGraph, mapGraph.getNode(currentPosition), mapGraph.getNode(origin), threshold);
                if (path != null && AStar.pathReachedGoal(path, origin)) {
                    distance = path.size();
                }
            } else if (currentPosition.dist(origin) <= threshold) {
                distance = currentPosition.dist(origin);
            }
        }
        return distance;
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
}
