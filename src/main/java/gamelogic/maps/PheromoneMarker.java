package gamelogic.maps;

import datastructures.Vector2D;
import gamelogic.agent.Agent;

public class PheromoneMarker implements MarkerInterface {
    private final double strengthReducePerSecond;
    private final Agent agent;
    private final double initialSmellingDistance;
    private final Vector2D position; 
    private double strength;

    public PheromoneMarker(Agent agent, Vector2D position, double initialSmellingDistance, double strengthReducePerSecond) {
        this.agent = agent;
        this.position = position;
        this.initialSmellingDistance = initialSmellingDistance;
        this.strength = 1;
        this.strengthReducePerSecond = strengthReducePerSecond;
    }

    public double getDistance() { return strength*initialSmellingDistance; }
    public double getStrength() { return strength; }

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