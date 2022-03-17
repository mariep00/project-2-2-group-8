package controller.maps;

import controller.Vector2D;

public interface MarkerInterface {
    public boolean updateMarker(double timeStep);
    public Vector2D getPosition();
}
