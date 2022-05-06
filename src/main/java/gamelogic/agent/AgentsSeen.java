package gamelogic.agent;

import datastructures.Vector2D;

public class AgentsSeen {
    private final Vector2D position;
    private int nrTimeStepsAgo;

    public AgentsSeen(Vector2D position, int nrTimeStepsAgo) {
        this.position = position;
        this.nrTimeStepsAgo = nrTimeStepsAgo;
    }
    public AgentsSeen(Vector2D position) {
        this.position = position;
        this.nrTimeStepsAgo = 0;
    }

    public void incrementNrTimeStepsAgo() {
        nrTimeStepsAgo++;
    }

    public Vector2D getPosition() { return position; }
    public int getNrTimeStepsAgo() { return nrTimeStepsAgo; }

    @Override
    public String toString() { return "Seen at position; " + position + ", time steps ago; " + nrTimeStepsAgo; }

}


