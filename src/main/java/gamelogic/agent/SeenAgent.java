package gamelogic.agent;

import datastructures.Vector2D;

public class SeenAgent {

    private Vector2D[] seenAgentPositions;

    public SeenAgent() {

    }

    public Vector2D getSeenAgentPosition(int agentIndex) {
        Vector2D seenAgentPosition = new Vector2D(0, 0);
        for (int i = 0; i < seenAgentPositions.length; i++) {
            if (seenAgentPositions[i].equals(agentIndex)) {
                seenAgentPosition = seenAgentPositions[i];
            }
        }
        return seenAgentPosition;
    }

    public Vector2D[] getSeenAgentPositions() {
        return seenAgentPositions;
    }

}
