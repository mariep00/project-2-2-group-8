package gamelogic.maps;

import datastructures.Vector2D;
import gamelogic.agent.Agent;

public interface MarkerInterface {
    void updateMarker(double timeStep);
    Vector2D getPosition();
    Agent getAgent();
    boolean shouldRemove();
}
