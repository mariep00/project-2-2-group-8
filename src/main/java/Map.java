public class Map {

    private int[][] mapGrid;
    public int width;
    public int height;

    public Map () {

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

    public void createMap(int width, int height) {
        mapGrid = new int[this.height][this.width];
    }

    public void insertElement(int x1, int y1, int x2, int y2, int elementId) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                mapGrid[j][i] = elementId;
            }
        }
    }
}