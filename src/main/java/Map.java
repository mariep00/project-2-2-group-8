public class Map {
    
    protected Tile[][] mapGrid;
    public int width;
    public int height;

    public Map () {
        
    }

    public Tile[][] getMap () {
        return mapGrid;
    }

    public void setMap (Tile[][] map) {
        mapGrid = map;
    }

    public Tile getTile (int x, int y) {
        return mapGrid[y][x];
    }

    public void setTile (int x, int y, Tile tile) {
        mapGrid[y][x] = tile;
    }

    public void createMap(int width, int height, float scaling) {
        //this.width = Math.round(width/scaling);
        //this.height = Math.round(height/scaling);
        this.width = width+1;
        this.height = height+1;
        mapGrid = new Tile[this.height][this.width];
    }

    public void insertElement(int x1, int y1, int x2, int y2, Tile tile) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                mapGrid[j][i] = tile;
            }
        }
    }

    public void insertElement(Vector2D[] points, Tile tile) {
        for (int i=0; i<points.length; i++) {
            mapGrid[points[i].getY()][points[i].getX()] = tile;
        }
    }



}
