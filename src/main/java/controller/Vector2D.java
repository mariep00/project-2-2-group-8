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

    public boolean equals(Vector2D other){
        return this.x == other.x && this.y == other.y;
    }

    public Vector2D getSide (Direction dir) {
        switch (dir) {
            case EAST:
                return new Vector2D(x+1, y);
            case NORTH:
                return new Vector2D(x, y-1);
            case SOUTH:
                return new Vector2D(x, y+1);
            case WEST:
                return new Vector2D(x-1, y);
        }
        return null;
    }

    /**
     * Method that gets the direction between two vectors
     * @param to        the vector to get the direction towards
     * @return          the direction of the current vector to the other vector
     */
    public Vector2D.Direction getDirection(Vector2D to) {
        if (to.x-this.x > 0) return Vector2D.Direction.EAST;
        else if (to.x-this.x < 0) return Vector2D.Direction.WEST;
        else if (to.y-this.y > 0) return Vector2D.Direction.SOUTH;
        else if (to.y-this.y < 0) return Vector2D.Direction.NORTH;
        else return null;
    }

    public enum Direction {
        NORTH,EAST,SOUTH,WEST;
    }
}
