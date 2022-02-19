public class Agent {
    private int position_X;
    private int position_Y;
    private int radius;
    private int speed;
    private int base_speed;
    private int sprint_speed;
    private double orientation;
    private Agent_AI agent_ai;

    public Agent(int position_X, int position_Y, int radius, int base_speed, int sprint_speed, double orientation, Agent_AI agent_ai) {
        this.position_X =position_X;
        this.position_Y = position_Y;
        this.radius = radius;
        this.speed = 0;
        this.base_speed = base_speed;
        this.sprint_speed = sprint_speed;
        this.orientation = orientation;
        this.agent_ai = agent_ai;
    }

    public int move() {
        return this.agent_ai.getMove();
    }

    public void walk() {
        this.speed = base_speed;
    }

    public void sprint() {
        this.speed = sprint_speed;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public boolean isHit(int target_point_X, int target_point_Y) {
        double distance = Math.sqrt(Math.pow((this.position_X - target_point_X), 2)) + Math.sqrt(Math.pow((this.position_Y - target_point_Y), 2));
        return distance <= this.radius;
    }

    public void tick() {
        switch (Double.toString(this.orientation)) {
            case "0" -> this.position_Y += this.speed;
            case "90" -> this.position_X += this.speed;
            case "180" -> this.position_Y -= this.speed;
            case "270" -> this.position_X -= this.speed;
        }
    }
}
