package gamelogic.maps;

import gamelogic.Vector2D;
import gamelogic.agent.Agent;

public interface MarkerInterface {
    void updateMarker(double timeStep);
    Vector2D getPosition();
    Agent getAgent();
    boolean shouldRemove();
}
