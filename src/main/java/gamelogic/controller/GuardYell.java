package gamelogic.controller;

import datastructures.Vector2D;

public class GuardYell {
    public final Vector2D position;
    public final int agentIndex;

    public GuardYell(Vector2D position, int agentIndex) {
        this.position = position;
        this.agentIndex = agentIndex;
    }
}
