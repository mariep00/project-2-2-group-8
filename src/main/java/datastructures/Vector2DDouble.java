package datastructures;

public class Vector2DDouble {
    public final double x;
    public final double y;

    public Vector2DDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2DDouble withLength(double length) {
        return unitVector().scale(length);
    }

    public Vector2DDouble scale(double scalar) {
        return new Vector2DDouble(x*scalar, y*scalar);
    }

    public Vector2DDouble unitVector() {
        double magnitude = magnitude();
        return new Vector2DDouble(x/magnitude, y/magnitude);
    }

    public double magnitude() {
        return Math.sqrt((x*x)+(y*y));
    }

    public Vector2D round() {
        return new Vector2D((int) Math.round(x), (int ) Math.round(y));
    }
}
