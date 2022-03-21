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
    

    /**
     * Euclidean distance
     * @param other
     * @return Returns the euclidean distance as a double
     */
    public double dist(Vector2D other) {
        return Math.sqrt(Math.pow(this.x-other.x, 2)+Math.pow(this.y-other.y, 2));
    }

    public int manhattanDist (Vector2D other) {
        return Math.abs(this.x-other.x)+Math.abs(this.y-other.y);
    }

    public double getAngleBetweenVector(Vector2D other) {
        Vector2D directionalVector = new Vector2D(other.x-this.x, other.y-this.y);
        double theta = Math.atan2(0, 0);
        double thetaWithEastToRight = theta-(Math.PI/2);
        double result = thetaWithEastToRight < 0 ? Math.toDegrees(thetaWithEastToRight)+360 : Math.toDegrees(thetaWithEastToRight);
        return result;
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
