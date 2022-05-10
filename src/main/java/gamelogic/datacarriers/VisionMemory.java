package gamelogic.datacarriers;

import datastructures.Vector2D;

public record VisionMemory(Vector2D position, double secondsAgo, double orientation) {
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            VisionMemory temp = (VisionMemory) other;
            return temp.position().equals(this.position()) && temp.secondsAgo() == this.secondsAgo() && temp.orientation() == this.orientation();
        }
        return false;
    }
}
