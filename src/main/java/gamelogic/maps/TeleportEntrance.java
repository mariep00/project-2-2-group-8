package gamelogic.maps;

import gamelogic.Vector2D;

public class TeleportEntrance extends SpecialFeature {
    private Vector2D exit;
    private double orientation;
    
    public TeleportEntrance(Vector2D exit, double orientation) {
        this.exit = exit;
        this.orientation = orientation;
    }

    public Vector2D getExit() {
        return exit;
    }

    public void setExit(Vector2D exit) {
        this.exit = exit;
    }

    public double getOrientation() {
        return this.orientation;
    }
}
