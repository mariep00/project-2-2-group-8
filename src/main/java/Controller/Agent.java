package Controller;

public class Agent {
    
    private int base_speed;
    private int sprint_speed;
    private double orientation;

    public Agent(int base_speed, int sprint_speed, double orientation) {
    
        this.base_speed = base_speed;
        this.sprint_speed = sprint_speed;
        this.orientation = orientation;
    }

    public int tick() {
        return 0;

    }
}