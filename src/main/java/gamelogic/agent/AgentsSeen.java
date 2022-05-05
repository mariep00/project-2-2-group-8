package gamelogic.agent;

import datastructures.Vector2D;
import gamelogic.controller.State;

public class AgentsSeen {
    private final Vector2D[] positions;
    private final int[] nrTimeStepsAgo;
    private final int agentIndex;

    public AgentsSeen(Vector2D[] positions, int agentIndex) {
        this.positions = positions;
        this.nrTimeStepsAgo = new int[positions.length];
        this.agentIndex = agentIndex;
    }

    public void tick(State state, Vector2D agentCurrentPosition) {
        for (int i = 0; i < nrTimeStepsAgo.length; i++) {
            if (i != agentIndex && positions[i] != null) {

                nrTimeStepsAgo[i]++;
            }
        }

    }

    public void updatePosition(int agenIndex, Vector2D position) {
        positions[agenIndex] = position;
        nrTimeStepsAgo[agenIndex] = 0;
    }
    public Vector2D[] getPositions() { return positions; }
    public int[] getNrTimeStepsAgo() { return nrTimeStepsAgo; }
}
