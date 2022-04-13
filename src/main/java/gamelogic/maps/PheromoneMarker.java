package gamelogic.maps;

import datastructures.Vector2D;
import gamelogic.agent.Agent;

public class PheromoneMarker implements MarkerInterface {
    public static final double strengthReducePerSecond = 1;
    private final Agent agent;
    private final int initialSmellingDistance;
    private final Vector2D position; 
    private double strength;

    public PheromoneMarker(Agent agent, Vector2D position, int initialSmellingDistance) {
        this.agent = agent;
        this.position = position;
        this.initialSmellingDistance = initialSmellingDistance;
        this.strength = 1;
    }

    public double getDistance() { return strength*initialSmellingDistance; }

    @Override
    public boolean shouldRemove() { return strength <= 0; }

    @Override
    public Vector2D getPosition() { return position; }

    @Override
    public Agent getAgent() { return agent; }

    @Override
    public void updateMarker(double timeStep) {
        strength -= (strengthReducePerSecond*timeStep);
    }
}