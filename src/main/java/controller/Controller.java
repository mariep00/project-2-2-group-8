package controller;

import controller.agent.Agent;
import controller.maps.EndingExplorationMap;
import controller.maps.MarkerInterface;
import controller.maps.PheromoneMarker;
import controller.maps.ScenarioMap;
import controller.maps.TeleportEntrance;
import controller.maps.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    private final Random rand = new Random();
    private FOV fov;
    private ScenarioMap scMap;
    protected EndingExplorationMap endingExplorationMap;
    private Vector2D[] agentSpawnLocations;
    protected Vector2D[] agentPositions;
    protected Agent[] agentsGuards;
    private Agent[] agentsIntruders;
    private double timestep;
    private double time;
    protected ArrayList<Vector2D>[] visions;
    protected ArrayList<Tile> tilesWithMarker;

    public Controller(ScenarioMap scMap) {
        this.scMap = scMap;
        this.agentSpawnLocations = new Vector2D[scMap.getNumGuards()];
        this.agentsGuards = new Agent[scMap.getNumGuards()];
        this.agentsIntruders = new Agent[scMap.getNumIntruders()];
        this.agentPositions = new Vector2D[scMap.getNumGuards()+scMap.getNumIntruders()];
        this.fov = new FOV(scMap.getGuardViewRange());
        this.endingExplorationMap = new EndingExplorationMap(this.scMap);
        this.timestep = scMap.getTimestep();
        this.visions = new ArrayList[scMap.getNumGuards()];
        this.tilesWithMarker = new ArrayList<>();

        createAgents(1, 2);
    }

    public void init() {
        spawnAgents();
        for (int i = 0; i < agentsGuards.length; i++) {
            updateVision(i);
            updateProgress(visions[i], i); // Set the beginning "progress"
        }
    }

    public void start() {
        engine();
    }

    protected ArrayList<Vector2D> calculateFOV(int agentIndex, Vector2D agentPosition) {
        return fov.calculate(agentsGuards[agentIndex].getView_angle(), agentsGuards[agentIndex].getView_range(), scMap.createAreaMap(agentPosition, agentsGuards[agentIndex].getView_range()), agentsGuards[agentIndex].getOrientation()).getInVision();
    }

    public void tick() {
        tick(timestep);
    }
    public void tick(double timestep) {
        for (int i=0; i<agentsGuards.length; i++) {
            ArrayList<Tile> tiles = getTilesInVision(visions[i], i);
            if (updateProgress(visions[i], i)) { break; }
            List<PheromoneMarker> pheromoneMarkersCloseEnough = getPheromoneMarkersCloseEnough(i);
            double[] pheromoneMarkerDirections = getPheromoneMarkersDirections(agentPositions[i], pheromoneMarkersCloseEnough);
            int task = agentsGuards[i].tick(tiles, convertRelativeCurrentPosToRelativeToSpawn(visions[i], i), pheromoneMarkersCloseEnough, pheromoneMarkerDirections, timestep);
            updateAgent(i, task);
            updateVision(i);
        }
        updateMarkers();
    }

    private List<PheromoneMarker> getPheromoneMarkersCloseEnough(int agentIndex) {
        ArrayList<PheromoneMarker> markersCloseEnough = new ArrayList<>();
        for (Tile tile : tilesWithMarker) {
            if (tile.getPheromoneMarker().getAgent() != agentsGuards[agentIndex]
                    && agentPositions[agentIndex].dist(tile.getPheromoneMarker().getPosition()) <= tile.getPheromoneMarker().getDistance()
                    && !isWallInBetween(agentPositions[agentIndex], tile.getPheromoneMarker().getPosition())) {
                markersCloseEnough.add(tile.getPheromoneMarker());
            }
        }
        return markersCloseEnough;
    }

    private double[] getPheromoneMarkersDirections(Vector2D agentPosition, List<PheromoneMarker> pheromoneMarkers) {
        double[] pheromoneMarkerDirections = new double[pheromoneMarkers.size()];
        for (int i = 0; i < pheromoneMarkers.size(); i++) {
            pheromoneMarkerDirections[i] = agentPosition.getAngleBetweenVector(pheromoneMarkers.get(i).getPosition());
        }
        return pheromoneMarkerDirections;
    }

    private boolean isWallInBetween(Vector2D begin, Vector2D end) {
        Vector2D[] positions = fov.calculateLine(begin, end);
        for (Vector2D pos : positions) {
            if (scMap.getTile(pos).isWall()) return true;
        }
        return false;
    }

    private ArrayList<Tile> getTilesInVision(ArrayList<Vector2D> positions, int agentIndex) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Vector2D vector2D : positions) {
            Vector2D abs = convertRelativeCurrentPosToAbsolute(vector2D, agentIndex);
            tiles.add(scMap.getTile(abs));
        }
        return tiles;
    }

    protected void updateVision(int agentIndex) {
        visions[agentIndex] = calculateFOV(agentIndex, agentPositions[agentIndex]);
    }

    protected void updateMarkers() {
        for (Tile tile : tilesWithMarker) {
            MarkerInterface[] markers = tile.getMarkers();
            for (int i = 0; i < markers.length; i++) {
                MarkerInterface marker = markers[i];
                if (marker.updateMarker(this.timestep)) tile.removeMarker(marker);
            }
        }
    }

    protected void updateAgent(int agentIndex, int task) {
        //0 - move forward
        //1 - turn 90deg
        //2 - turn 180deg
        //3 - turn 270deg

        switch (task) {
            case 0:
                updateAgentPosition(agentIndex, agentMoveForward(agentIndex));
                break;
            case 1:
                updateAgentOrientation(agentIndex, 90);
                break;
            case 2:
                updateAgentOrientation(agentIndex, 180);
                break;
            case 3:
                updateAgentOrientation(agentIndex,270);
                break;
        }

        addMarker(agentPositions[agentIndex], new PheromoneMarker(agentsGuards[agentIndex], agentPositions[agentIndex], scMap.getSmellingDistance()));
    }

    protected void addMarker(Vector2D position, MarkerInterface marker) {
        scMap.getTile(position).addMarker(marker);
    }

    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        agentPositions[agentIndex] = pos;
        agentsGuards[agentIndex].updatePosition(convertAbsoluteToRelativeSpawn(pos, agentIndex));
    }
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        agentsGuards[agentIndex].updateOrientation(orientationToAdd);
    }

    protected boolean updateProgress(Vector2D vector, int agentIndex) {
        return endingExplorationMap.updateExplorationMap(convertRelativeCurrentPosToAbsolute(vector, agentIndex));
    }
    private boolean updateProgress(ArrayList<Vector2D> positions, int agentIndex) {
        for (Vector2D vector2D : positions) {
            if (updateProgress(vector2D, agentIndex)) {
                return true;
            }
        }
        return false;
    }

    private Vector2D agentMoveForward(int agentIndex) {
        int numberOfSteps = getNumberOfSteps(agentIndex);
        Vector2D lastPos = agentPositions[agentIndex];
        for (int i = 1; i <= numberOfSteps; i++) {
            Vector2D pos = agentPositions[agentIndex].getSide(agentsGuards[agentIndex].getOrientation(), i);
            if (pos.x >= scMap.getWidth() || pos.x < 0 || pos.y >= scMap.getHeight() || pos.y < 0) return lastPos;
            Tile tileAtPos = scMap.getTile(pos);           
            if (tileAtPos.isWall()) return lastPos;
            if (tileAtPos.isTeleportEntrance()) {
                agentsGuards[agentIndex].creatTeleportDestinationNode(convertAbsoluteToRelativeSpawn(pos, agentIndex), convertAbsoluteToRelativeSpawn(((TeleportEntrance) tileAtPos.getFeature()).getExit(), agentIndex), tileAtPos, scMap.getTile(((TeleportEntrance) tileAtPos.getFeature()).getExit()));
                return posAfterTeleport(agentIndex, tileAtPos);
            }
            if (isAgentAtPos(pos)) return lastPos;
            else lastPos = pos;
        }
        return lastPos;
    }

    private Vector2D posAfterTeleport(int agentIndex, Tile tileAtPos) {
        TeleportEntrance tp = (TeleportEntrance)tileAtPos.getFeature();
        agentsGuards[agentIndex].changeOrientation(tp.getOrientation());
        return tp.getExit();
    }

    private int getNumberOfSteps(int agentIndex) {
        double numberOfSteps = agentsGuards[agentIndex].getBase_speed()*timestep;
        if (numberOfSteps < 1) return 1;
        else return (int) Math.round(numberOfSteps);
    }

    private boolean isAgentAtPos(Vector2D pos) {
        for (int i = 0; i < agentPositions.length; i++) {
            if (agentPositions[i].equals(pos)) return true;
        }
        return false;
    }

    public void engine(){
        while(!endingExplorationMap.isExplored()){
            tick(timestep);
            time += timestep;
        }
        end();
    }

    protected void end() {
        int hours = (int) time / 3600;
        int minutes = ((int)time % 3600) / 60;
        double seconds = time % 60;
        System.out.println("Everything is explored. It took " + hours + " hour(s) " + minutes + " minutes " + seconds + " seconds.");
        System.out.println(agentsGuards[0].toString());
    }

    protected void spawnAgents() {
        Random random = new Random();
        ArrayList<Integer> indicesUsed = new ArrayList<>();
        ArrayList<Vector2D> spawnAreaGuards = scMap.getSpawnAreaGuards();

        for (int i = 0; i < agentsGuards.length; i++) {
            while (true) {
                int rand = random.nextInt(spawnAreaGuards.size());
                if (!indicesUsed.contains(rand)) {
                    indicesUsed.add(rand);
                    agentSpawnLocations[i] = spawnAreaGuards.get(rand);
                    agentPositions[i] = agentSpawnLocations[i];
                    break;
                }
            }
        }
    }

    private void createAgents(int brainIntruders, int brainGuards) {
        int[] orientations = {0, 90, 180, 270};
        if (scMap.getGameMode() != 0) {
            for (int i=0; i<scMap.getNumIntruders(); i++) {
                agentsIntruders[i] = new Agent(scMap.getBaseSpeedIntruder(), scMap.getSprintSpeedIntruder(), scMap.getIntruderViewAngle(), scMap.getIntruderViewRange(), orientations[rand.nextInt(orientations.length)], brainIntruders);
            }
        }
        
        for (int i=0; i<scMap.getNumGuards(); i++) {
            agentsGuards[i] = new Agent(scMap.getBaseSpeedGuard(), 0.0, scMap.getGuardViewAngle(),scMap.getGuardViewRange(),orientations[rand.nextInt(orientations.length)], brainGuards);
        }
    }

    public Vector2D convertRelativeSpawnToAbsolute(Vector2D relPos, int agentId) {
        return relPos.add(agentSpawnLocations[agentId]);
    }
    public ArrayList<Vector2D> convertRelativeSpawnToAbsolute(ArrayList<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeSpawnToAbsolute(vector2D, agentId));
        }
        return absPos;
    }

    public Vector2D convertAbsoluteToRelativeSpawn(Vector2D absPos, int agentId) {
        return absPos.subtract(agentSpawnLocations[agentId]);
    }

    public Vector2D convertRelativeCurrentPosToAbsolute(Vector2D relPos, int agentId) {
        return relPos.add(agentPositions[agentId]);
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToAbsolute(ArrayList<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToAbsolute(vector2D, agentId));
        }
        return absPos;
    }

    public Vector2D convertRelativeCurrentPosToRelativeToSpawn(Vector2D relPos, int agentId) {
        return convertAbsoluteToRelativeSpawn(relPos.add(agentPositions[agentId]), agentId);
    }
    public ArrayList<Vector2D> convertRelativeCurrentPosToRelativeToSpawn (ArrayList<Vector2D> relPos, int agentId) {
        ArrayList<Vector2D> absPos = new ArrayList<>();
        for (Vector2D vector2D : relPos) {
            absPos.add(convertRelativeCurrentPosToRelativeToSpawn(vector2D, agentId));
        }
        return absPos;
    }
}
