package gamelogic.agent;

import datastructures.Vector2D;

public class SeenAgent {
    private Vector2D position;
    private int nrTimeStepsAgo;

    public SeenAgent(Vector2D position) {
        this.position = position;
        this.nrTimeStepsAgo = 0;
    }

    public void tick(Vector2D agentCurrentPosition) {
        nrTimeStepsAgo++;

    }

    public Vector2D getPosition() { return position; }
    public int getNrTimeStepsAgo() { return nrTimeStepsAgo; }
}
