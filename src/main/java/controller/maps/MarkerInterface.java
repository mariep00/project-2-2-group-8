package controller.maps;

import controller.Vector2D;
import controller.agent.Agent;

public interface MarkerInterface {
    void updateMarker(double timeStep);
    Vector2D getPosition();
    Agent getAgent();
    boolean shouldRemove();
}
