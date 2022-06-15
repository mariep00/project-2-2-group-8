package gamelogic.datacarriers;

import datastructures.Vector2D;
import gui.gamescreen.AgentType;

public record VisionMemory(Vector2D position, double secondsAgo, double orientation, AgentType agentType) {
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            VisionMemory temp = (VisionMemory) other;
            return temp.position().equals(this.position()) && temp.secondsAgo() == this.secondsAgo() && temp.orientation() == this.orientation();
        }
        return false;
    }

    public int compareTo(VisionMemory other) {
        if (other == null) return 1;
        else if (secondsAgo < other.secondsAgo) return 1;
        else if (secondsAgo == other.secondsAgo) return position.magnitude() < other.position.magnitude() ? 1 : 0;
        return -1;
    }
}
