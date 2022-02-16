public class ExplorationMap extends Map {

    private int origin_X; // origin: top left
    private int origin_Y;

    public ExplorationMap(int origin_X, int origin_Y, int height, int width) {
        super(height, width);
        this.origin_X = origin_X;
        this.origin_Y = origin_Y;
    }

    private void leftExtend() {
        int[][] tmp = new int[this.height][this.width*2];
        for(int i = 0; i < this.mapGrid.length; i++) {
            for(int j = 0; j < this.mapGrid[i].length; j++) {
                tmp[i][j+this.width] = this.mapGrid[i][j];
            }
        }
        this.width = this.width*2;
        this.origin_Y = this.origin_Y + this.height;
        setMap(tmp);
    }

    private void rightExtend() {
        int[][] tmp = new int[this.height][this.width*2];
        for(int i = 0; i < this.mapGrid.length; i++) {
            for(int j = 0; j < this.mapGrid[i].length; j++) {
                tmp[i][j] = this.mapGrid[i][j];
            }
        }
        this.width = this.width*2;
        setMap(tmp);
    }

    private void topExtend() {
        int[][] tmp = new int[this.height*2][this.width];
        for(int i = 0; i < this.mapGrid.length; i++) {
            for(int j = 0; j < this.mapGrid[i].length; j++) {
                tmp[i+this.height][j] = this.mapGrid[i][j];
            }
        }
        this.height = this.height*2;
        this.origin_X = this.origin_X + this.height;
        setMap(tmp);
    }

    private void bottomExtend() {
        int[][] tmp = new int[this.height*2][this.width];
        for(int i = 0; i < this.mapGrid.length; i++) {
            for(int j = 0; j < this.mapGrid[i].length; j++) {
                tmp[i][j] = this.mapGrid[i][j];
            }
        }
        this.height = this.height*2;
        setMap(tmp);
    }

    public void insertElement(int x1, int y1, int elementId) {
        if((x1 - this.origin_X) < 0) {
            this.leftExtend();
        }
        else if((x1 - this.origin_X) > this.getWidth()) {
            this.rightExtend();
        }
        else if((y1 - this.origin_Y) < 0) {
            this.topExtend();
        }
        else if((y1 - this.origin_Y) > this.getHeight()) {
            this.bottomExtend();
        }

        this.mapGrid[x1][y1] = elementId;
    }
}