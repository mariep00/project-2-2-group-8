package Controller;

public class Vector2D {
    
    public final int x;
    public final int y;

    public Vector2D (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2D [x=" + x + ", y=" + y + "]";
    }

    public Vector2D subtract (Vector2D other) {
        return new Vector2D(x-other.getX(), y-other.getY());
    }

    public boolean equals(Vector2D other){
        return (this.x == other.x && this.y == other.y);
    }
}
