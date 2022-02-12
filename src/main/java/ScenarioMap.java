import java.util.ArrayList;

public class ScenarioMap extends Map{

    private String name = "";
    private int gameMode = 0;
    private int numGuards;
    private int numIntruders;
    private double baseSpeedIntruder;
    private double baseSpeedGuard;
    private double sprintSpeedIntruder;
    private ArrayList<Teleport> teleporters;

    public ScenarioMap() {
        teleporters = new ArrayList<Teleport>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getNumGuards() {
        return numGuards;
    }

    public void setNumGuards(int numGuards) {
        this.numGuards = numGuards;
    }

    public int getNumIntruders() {
        return numIntruders;
    }

    public void setNumIntruders(int numIntruders) {
        this.numIntruders = numIntruders;
    }

    public double getBaseSpeedIntruder() {
        return baseSpeedIntruder;
    }

    public void setBaseSpeedIntruder(double baseSpeedIntruder) {
        this.baseSpeedIntruder = baseSpeedIntruder;
    }

    public double getBaseSpeedGuard() {
        return baseSpeedGuard;
    }

    public void setBaseSpeedGuard(double baseSpeedGuard) {
        this.baseSpeedGuard = baseSpeedGuard;
    }

    public double getSprintSpeedIntruder() {
        return sprintSpeedIntruder;
    }

    public void setSprintSpeedIntruder(double sprintSpeedIntruder) {
        this.sprintSpeedIntruder = sprintSpeedIntruder;
    }

    public Teleport getTeleport (int index) {
        return teleporters.get(index-8);
    }

    public void setTeleport (int x1, int y1, int x2, int y2, int x3, int y3, double rotation) {
        Teleport tmp = new Teleport(x1, y1, x2, y2, x3, y3, rotation);
        teleporters.add(tmp);
        tmp.setIndex(teleporters.indexOf(tmp)+8);
        insertElement(x1, y1, x2, y2, tmp.getIndex());
    }

}
