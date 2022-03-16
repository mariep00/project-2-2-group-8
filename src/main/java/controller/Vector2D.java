package controller;

public class Vector2D {
    
    public final int x;
    public final int y;

    public Vector2D (int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2D [x=" + x + ", y=" + y + "]";
    }

    public Vector2D subtract (Vector2D other) {
        return new Vector2D(x-other.x, y-other.y);
    }
    public Vector2D add (Vector2D other) { return new Vector2D(x+other.x, y+other.y); }

    public boolean equals(Vector2D other){
        return this.x == other.x && this.y == other.y;
    }

    public Vector2D getSide(double dir) {
        return getSide(dir, 1);
    }
    public Vector2D getSide (double dir, int steps) {
        //0 - move forward
        //1 - turn 90deg
        //2 - turn 180deg
        //3 - turn 270deg
        switch ((int)dir) {
            case 0:
                return new Vector2D(x+steps, y);
            case 270:
                return new Vector2D(x, y-steps);
            case 90:
                return new Vector2D(x, y+steps);
            case 180:
                return new Vector2D(x-steps, y);
        }
        return null;
    }

    public Vector2D[] getNeighbours () {
        Vector2D[] neighbours = new Vector2D[4];
        neighbours[0] = new Vector2D(this.x, (this.y+1));
        neighbours[1] = new Vector2D((this.x+1), this.y);
        neighbours[2] = new Vector2D(this.x, (this.y-1));
        neighbours[3] = new Vector2D((this.x-1), (this.y));
        return neighbours;
    }
}
