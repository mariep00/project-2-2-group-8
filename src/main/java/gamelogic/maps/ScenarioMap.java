package gamelogic.maps;

import datastructures.Vector2D;
import gamelogic.maps.Tile.Type;

import java.util.ArrayList;

public class ScenarioMap {
    private String fileName = "";
    private String mapName = "";
    private int gameMode = 0;
    private int numGuards;
    private int numIntruders;
    private double baseSpeedIntruder;
    private double baseSpeedGuard;
    private double sprintSpeedIntruder;
    private double guardViewAngle = 90;
    private double guardViewRange = 12;
    private double intruderViewAngle = 90;
    private double intruderViewRange = 12;
    private double timestep;
    private ArrayList<Vector2D> spawnAreaGuards = new ArrayList<>();
    private ArrayList<Vector2D> spawnAreaIntruders = new ArrayList<>();
    private int numberMarkers = 5;
    private double shadedReduction = 0.5;
    private int footstepMaxHearingDistance = 16;
    private int rotatingMaxHearingDistance = 9;
    private int yellMaxHearingDistance = 50;
    private double soundStandardDeviation = 10;
    private int pheromoneMaxSmellingDistance = 10;
    private double pheromoneReduction = 0.1;

    private Tile[][] mapGrid;
    private int width;
    private int height;

    public ScenarioMap() { }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
    
    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
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

    public double getShadedReduction() { return shadedReduction; }

    public int getFootstepMaxHearingDistance() { return footstepMaxHearingDistance; }

    public int getYellMaxHearingDistance() { return yellMaxHearingDistance; }

    public double getSoundStandardDeviation() { return soundStandardDeviation; }

    public int getPheromoneMaxSmellingDistance() { return pheromoneMaxSmellingDistance; }

    public double getPheromoneReduction() { return pheromoneReduction; }

    public int getRotatingMaxHearingDistance() { return rotatingMaxHearingDistance; }

    public void setShadedReduction(double shadedReduction) { this.shadedReduction = shadedReduction; }

    public void setFootstepMaxHearingDistance(int footstepMaxHearingDistance) { this.footstepMaxHearingDistance = footstepMaxHearingDistance; }

    public void setYellMaxHearingDistance(int yellMaxHearingDistance) { this.yellMaxHearingDistance = yellMaxHearingDistance; }

    public void setSoundStandardDeviation(double soundStandardDeviation) { this.soundStandardDeviation = soundStandardDeviation; }

    public void setPheromoneMaxSmellingDistance(int pheromoneMaxSmellingDistance) { this.pheromoneMaxSmellingDistance = pheromoneMaxSmellingDistance; }

    public void setPheromoneReduction(double pheromoneReduction) { this.pheromoneReduction = pheromoneReduction; }

    public void setRotatingMaxHearingDistance(int rotatingMaxHearingDistance) { this.rotatingMaxHearingDistance = rotatingMaxHearingDistance; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public void setBaseSpeedGuard(double baseSpeedGuard) {
        if (timestep == 0.0) {
            timestep = 1.0/baseSpeedGuard;
        }

        this.baseSpeedGuard = baseSpeedGuard;
    }

    public double getSprintSpeedIntruder() {
        return sprintSpeedIntruder;
    }

    public void setSprintSpeedIntruder(double sprintSpeedIntruder) {
        this.sprintSpeedIntruder = sprintSpeedIntruder;
    }

    public void setTimeStep(double timestep){
        this.timestep = timestep;
    }
    public double getTimestep(){
        return timestep;
    }

    public Tile[][] getMapGrid() {
        return mapGrid;
    }

    public ArrayList<Vector2D> getSpawnAreaGuards() {
        return spawnAreaGuards;
    }

    public ArrayList<Vector2D> getSpawnAreaIntruders() {
        return spawnAreaIntruders;
    }

    public double getGuardViewAngle() {
        return guardViewAngle;
    }

    public double getGuardViewRange() {
        return guardViewRange;
    }

    public double getIntruderViewAngle() {
        return intruderViewAngle;
    }

    public double getIntruderViewRange() {
        return intruderViewRange;
    }

    public void setGuardViewAngle(double guardViewAngle) {
        this.guardViewAngle = guardViewAngle;
    }

    public void setGuardViewRange(double guardViewRange) {
        this.guardViewRange = guardViewRange;
    }

    public void setIntruderViewAngle(double intruderViewAngle) {
        this.intruderViewAngle = intruderViewAngle;
    }

    public void setIntruderViewRange(double intruderViewRange) {
        this.intruderViewRange = intruderViewRange;
    }

    public void setTeleport (int x1, int y1, int x2, int y2, int x3, int y3, double rotation) {
        TeleportEntrance tmp = new TeleportEntrance(new Vector2D(x3, y3), rotation);
        for (int i=y1; i<=y2; i++) {
            for (int j=x1; j<=x2; j++) {
                mapGrid[i+1][j+1].setSpecialFeature(tmp);
                mapGrid[i+1][j+1].setType(Tile.Type.TELEPORT_ENTRANCE);;
            }
        } 
        mapGrid[y3+1][x3+1].setSpecialFeature(new TeleportExit(tmp));
        mapGrid[y3+1][x3+1].setType(Type.TELEPORT_EXIT);
    }

    public void setShaded (int x1, int y1, int x2, int y2) {
        for (int i=y1; i<=y2; i++) {
            for (int j=x1; j<=x2; j++) {
                mapGrid[i+1][j+1].setShaded(true);
            }
        }
    }
    public void setShaded(int x, int y) {
        mapGrid[y+1][x+1].setShaded(true);
    }

    public boolean checkWall(Vector2D pos) {
        return mapGrid[pos.y][pos.x].getType()==Tile.Type.WALL;
    }

    public Tile[][] getMap() {
        return mapGrid;
    }

    public void setMap(Tile[][] map) {
        mapGrid = map;
    }

    public Tile getTile(Vector2D pos) {
        return mapGrid[pos.y][pos.x];
    }

    public void setTile(int x, int y, Object tile) {
        mapGrid[y+1][x+1] = (Tile)tile;
    }

    public void createMap(int width, int height) {
        this.width = width+2;
        this.height = height+2;

        mapGrid = new Tile[this.height][this.width];

        for (int i=0; i < mapGrid[0].length-1; i++) {
            for (int j=0; j < mapGrid.length-1; j++) {
                mapGrid[j][i] = new Tile();
            }
        }
        for (int j = 0; j < mapGrid.length; j++) {
            mapGrid[j][0] = new Tile(Type.WALL, false);
            mapGrid[j][mapGrid[0].length - 1] = new Tile(Type.WALL, false);
        }
        for (int i = 0; i < mapGrid[0].length; i++) {
            mapGrid[0][i] = new Tile(Type.WALL, false);
            mapGrid[mapGrid.length - 1][i] = new Tile(Type.WALL, false);
        }
    }

    public void insertElement(int x1, int y1, int x2, int y2, Type type) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                mapGrid[j+1][i+1].setType(type);;
            }
        }
    }

    public void insertElement(int x, int y, Type type) {
        mapGrid[y+1][x+1].setType(type);
    }

    public void insertSpawnAreaGuard(int x1, int y1, int x2, int y2) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                spawnAreaGuards.add(new Vector2D(i+1, j+1));
            }
        }
    }
    public void insertSpawnAreaGuard(int x, int y) {
        spawnAreaGuards.add(new Vector2D(x+1, y+1));
    }

    public void insertSpawnAreaIntruder(int x1, int y1, int x2, int y2) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                spawnAreaIntruders.add(new Vector2D(i+1, j+1));
            }
        }
    }
    public void insertSpawnAreaIntruder(int x, int y) {
        spawnAreaIntruders.add(new Vector2D(x+1, y+1));
    }

    public void insertElement(Vector2D[] points, Type type) {
        for (int i=0; i<points.length; i++) {
            mapGrid[points[i].y+1][points[i].x+1].setType(type);
        }
        
    }

    public VisionMap createAreaMap (Vector2D position, double visionRange) {
        VisionMap areaMap = new VisionMap(visionRange);

        Vector2D start = new Vector2D(position.x-(int)visionRange, position.y-(int)visionRange);
        int endX = start.x+(2*(int)visionRange+1);
        int endY = start.y+(2*(int)visionRange+1);
        for (int x=start.x; x<endX; x++) {
            for (int y=start.y; y<endY; y++) {
                if (y<0 || x<0 || y>=height || x>=width) {
                    //areaMap.insertElement(new Vector2D(Math.abs(x)-Math.abs(start.x), Math.abs(y)-Math.abs(start.y)), 1);
                    areaMap.insertElement(new Vector2D(Math.abs(x-start.x), Math.abs(y-start.y)), 2);
                } else if (mapGrid[y][x].getType()==Type.WALL) {
                    areaMap.insertElement(new Vector2D(x-start.x, y-start.y), 1);
                } else if (mapGrid[y][x].isShaded()) {
                    areaMap.insertElement(new Vector2D(x-start.x, y-start.y), 3);
                }
            }
        }
        return areaMap;
    }

    public void setNumberMarkers(int numMarkers) {
        numberMarkers = numMarkers;
    }
}
