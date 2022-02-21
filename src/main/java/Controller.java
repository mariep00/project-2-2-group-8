import java.util.ArrayList;

public class Controller {
    
    private FOV fov;
    private ScenarioMap scMap;
    ArrayList<Vector2D> agentSpawnLocations;

    public Controller () {
        
    }

    public void calculateFOV(Vector2D agentPosition) {

    }

    public Tile checkType (Vector2D relPos, int agentId) {
        Vector2D pos = translatePosition(relPos, agentId);
        return scMap.checkType(pos);
    }

    private Vector2D translatePosition(Vector2D relPos, int agentId) {
        int x = agentSpawnLocations.get(agentId).getX()+relPos.getX();
        int y = agentSpawnLocations.get(agentId).getY()+relPos.getY();
        return new Vector2D(x, y);
    }

    public void tick() {

    }

}
