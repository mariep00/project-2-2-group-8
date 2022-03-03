package Controller;

import Controller.Tile.Type;

public class ScenarioMap {

    private String name = "";
    private int gameMode = 0;
    private int numGuards;
    private int numIntruders;
    private double baseSpeedIntruder;
    private double baseSpeedGuard;
    private double sprintSpeedIntruder;
    private double timestep;

    private Tile[][] mapGrid;
    private int width;
    private int height;
    private double scaling;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

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
        for (int i=0; i < mapGrid.length; i++) {
            for (int j =0; j < mapGrid[i].length; j++) {
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

    public void insertElement(Vector2D[] points, Type type) {
        for (int i=0; i<points.length; i++) {
            mapGrid[points[i].y][points[i].x].setType(type);;
        }
        
    }

    public VisionMap createAreaMap (Vector2D position, double visionRange) {
        VisionMap areaMap = new VisionMap(visionRange);

        Vector2D start = new Vector2D(position.x-(int)visionRange, position.y-(int)visionRange);
        int endX = start.x+(2*(int)visionRange+1);
        int endY = start.y+(2*(int)visionRange+1);
        for (int x=start.x; x<endX; x++) {
            for (int y=start.y; y<endY; y++) {
                if (y<0||x<0) {
                    areaMap.insertElement(new Vector2D(Math.abs(x)-Math.abs(start.x), Math.abs(y)-Math.abs(start.y)), 1);
                } else if (mapGrid[y][x].getType()==Type.WALL) {
                    areaMap.insertElement(new Vector2D(x-start.x, y-start.y), 1);
                }
            }
        }
        return areaMap;
    }

}
