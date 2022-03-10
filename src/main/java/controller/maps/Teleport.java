package controller.maps;

import controller.Vector2D;

public class Teleport extends SpecialFeature {

    private Vector2D exit;
    private double orientation;
    
    public Teleport (Vector2D exit, double orientation) {
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
