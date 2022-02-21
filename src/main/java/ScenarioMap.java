import java.util.ArrayList;

public class ScenarioMap implements MapInterface{

    private String name = "";
    private int gameMode = 0;
    private int numGuards;
    private int numIntruders;
    private double baseSpeedIntruder;
    private double baseSpeedGuard;
    private double sprintSpeedIntruder;
    private ArrayList<Teleport> teleporters;

    private Tile[][] mapGrid;
    private int width;
    private int height;
    private double scaling;

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
        Tile tile = new Tile(Tile.Type.TELEPORT);
        tile.setIndex(tmp.getIndex());
        insertElement(x1, y1, x2, y2, tile);
    }

    public void setShaded (int x1, int y1, int x2, int y2) {
        for (int i=y1; i<y2; i++) {
            for (int j=x1; j<x2; j++) {
                mapGrid[i][j].setShaded(true);
            }
        }
    }

    public boolean checkWall(Vector2D pos) {
        return mapGrid[pos.getY()][pos.getX()].getType()==Tile.Type.WALL;
    }

    @Override
    public Tile[][] getMap() {
        return mapGrid;
    }

    @Override
    public void setMap(Object[][] map) {
        mapGrid = (Tile[][])map;
    }

    @Override
    public Tile getTile(int x, int y) {
        return mapGrid[y][x];
    }

    @Override
    public void setTile(int x, int y, Object tile) {
        mapGrid[y][x] = (Tile)tile;
        
    }

    @Override
    public void createMap(int width, int height, float scaling) {
        //this.width = Math.round(width/scaling);
        //this.height = Math.round(height/scaling);
        this.width = width+1;
        this.height = height+1;
        mapGrid = new Tile[this.height][this.width];
    }

    @Override
    public void insertElement(int x1, int y1, int x2, int y2, Object tile) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                mapGrid[j][i] = (Tile)tile;
            }
        }
    }

    @Override
    public void insertElement(Vector2D[] points, Object tile) {
        for (int i=0; i<points.length; i++) {
            mapGrid[points[i].getY()][points[i].getX()] = (Tile)tile;
        }
        
    }

    public VisionMap createAreaMap (Vector2D position, double visionRange) {
        VisionMap areaMap = new VisionMap(visionRange);
        Vector2D topLeft = areaMap.translateCoordinateByCenter(new Vector2D(0, 0));
        return areaMap;
    }

}
