package gamelogic.controller;

import datastructures.Vector2D;

public class Yell {
    public final Vector2D position;
    public final int agentIndex;

    public Yell(Vector2D position, int agentIndex) {
        this.position = position;
        this.agentIndex = agentIndex;
    }
}
