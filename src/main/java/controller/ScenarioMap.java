package controller;

import controller.Tile.Type;

import java.util.ArrayList;

public class ScenarioMap {

    private String name = "";
    private int gameMode = 0;
    private int numGuards;
    private int numIntruders;
    private double baseSpeedIntruder;
    private double baseSpeedGuard;
    private double sprintSpeedIntruder;
    private double timestep;
    private ArrayList<Vector2D> spawnAreaGuards = new ArrayList<>();
    private ArrayList<Vector2D> spawnAreaIntruders = new ArrayList<>();

    private Tile[][] mapGrid;
    private int width;
    private int height;
    private double scaling;

    public ScenarioMap() {

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

    public void setTimeStep(double timestep){
        this.timestep = timestep;
    }
    public double getTimestep(){
        return timestep;
    }

    public Tile[][] getMapGrid() {
        return mapGrid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Vector2D> getSpawnAreaGuards() {
        return spawnAreaGuards;
    }

    public ArrayList<Vector2D> getSpawnAreaIntruders() {
        return spawnAreaIntruders;
    }

    public void setTeleport (int x1, int y1, int x2, int y2, int x3, int y3, double rotation) {
        Teleport tmp = new Teleport(new Vector2D(x3, y3), rotation);
        for (int i=y1; i<=y2; i++) {
            for (int j=x1; j<=x2; j++) {
                mapGrid[i][j].setSpecialFeature(tmp);
                mapGrid[i][j].setType(Tile.Type.TELEPORT);;
            }
        } 
        
    }

    public void setShaded (int x1, int y1, int x2, int y2) {
        for (int i=y1; i<=y2; i++) {
            for (int j=x1; j<=x2; j++) {
                mapGrid[i][j].setShaded(true);
            }
        }
    }
    public void setShaded(int x, int y) {
        mapGrid[y][x].setShaded(true);
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
        mapGrid[y][x] = (Tile)tile;
        
    }

    public void createMap(int width, int height, float scaling) {
        this.width = width+1;
        this.height = height+1;
        mapGrid = new Tile[this.height][this.width];
        for (int i=0; i < mapGrid[0].length; i++) {
            for (int j=0; j < mapGrid.length; j++) {
                mapGrid[j][i] = new Tile();
            }
        }
    }

    public void insertElement(int x1, int y1, int x2, int y2, Type type) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                mapGrid[j][i].setType(type);;
            }
        }
    }

    public void insertElement(int x, int y, Type type) {
        mapGrid[y][x].setType(type);
    }

    public void insertSpawnAreaGuard(int x1, int y1, int x2, int y2) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                spawnAreaGuards.add(new Vector2D(i, j));
            }
        }
    }
    public void insertSpawnAreaGuard(int x, int y) {
        spawnAreaGuards.add(new Vector2D(x, y));
    }

    public void insertSpawnAreaIntruder(int x1, int y1, int x2, int y2) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                spawnAreaIntruders.add(new Vector2D(i, j));
            }
        }
    }
    public void insertSpawnAreaIntruder(int x, int y) {
        spawnAreaIntruders.add(new Vector2D(x, y));
    }

    public void insertElement(Vector2D[] points, Type type) {
        for (int i=0; i<points.length; i++) {
            mapGrid[points[i].y][points[i].x].setType(type);;
        }
        
    }

    public VisionMap createAreaMap (Vector2D position, double visionRange) {
        VisionMap areaMap = new VisionMap(visionRange);
        Vector2D topLeft = areaMap.translateCoordinateByCenter(new Vector2D(0, 0));
        return areaMap;
    }

}
