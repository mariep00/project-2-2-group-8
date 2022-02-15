public class Map {

    protected int[][] mapGrid;
    protected int height;
    protected int width;

    public Map(int height, int width) {
        this.height = height;
        this.width = width;
        mapGrid = new int[this.height][this.width];
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int[][] getMap() {
        return mapGrid;
    }

    public void setMap(int [][] newMap) {
        this.mapGrid = newMap;
    }

    public int getTile (int x, int y) {
        return mapGrid[y][x];
    }

    public void insertElement(int x1, int y1, int x2, int y2, int elementId) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                mapGrid[j][i] = elementId;
            }
        }
    }
}