public class Agent {
    private double position_X;
    private double position_Y;
    private double radius;
    private double speed;
    private double base_speed;
    private double sprint_speed;
    private String orientation;
    private Agent_AI agent_ai;

    public Agent(double position_X, double position_Y, double radius, double base_speed, double sprint_speed, String orientation, Agent_AI agent_ai) {
        this.position_X =position_X;
        this.position_Y = position_Y;
        this.radius = radius;
        this.speed = 0.0;
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

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public boolean isHit(double target_point_X, double target_point_Y) {
        double distance = Math.sqrt(Math.pow((this.position_X - target_point_X), 2)) + Math.sqrt(Math.pow((this.position_Y - target_point_Y), 2));
        return distance <= this.radius;
    }

    public void tick() {
        switch (this.orientation) {
            case "NORTH" -> this.position_Y += this.speed;
            case "EAST" -> this.position_X += this.speed;
            case "SOUTH" -> this.position_Y -= this.speed;
            case "WEST" -> this.position_X -= this.speed;
        }
    }
}
