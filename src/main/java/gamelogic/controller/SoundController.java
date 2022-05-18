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

                LinkedList<Vector2D> path = AStar.calculate(mapGraph, mapGraph.getNode(controller.currentState.getAgentPosition(agentIndex)), mapGraph.getNode(soundOrigin.origin()), threshold-1);

                if (path != null && AStar.pathReachedGoal(path, soundOrigin.origin())) {
                    double angle = controller.getCurrentState().getAgentPosition(agentIndex).getAngleBetweenVector(soundOrigin.origin());
                    double angleNormalDistributed = Controller.addNoise(angle, soundStandardDeviation);
                    double maxThreshold = Math.max(footstepMaxHearingDistance, rotatingMaxHearingDistance); // Divide by maximum to have a normalised loudness
                    sounds.add(new Sound(angleNormalDistributed >= 360 ? angleNormalDistributed - 360 : angleNormalDistributed, ((float) maxThreshold - path.size()) / maxThreshold));
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
                LinkedList<Vector2D> path = AStar.calculate(mapGraph, mapGraph.getNode(controller.currentState.getAgentPosition(agentIndex)), mapGraph.getNode(guardYell.origin()), yellMaxHearingDistance);
                if (path != null && AStar.pathReachedGoal(path, guardYell.origin())) {
                    double angleWithNoise = Controller.addNoise(currentPos.getAngleBetweenVector(guardYell.origin()), soundStandardDeviation);
                    anglesOfGuardYell.add(new Sound(angleWithNoise >= 360 ? angleWithNoise - 360 : angleWithNoise, ((float) yellMaxHearingDistance-path.size()) / yellMaxHearingDistance));
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
}
