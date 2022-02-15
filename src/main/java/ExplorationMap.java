public class ExplorationMap extends Map {

    private int origin_X; // origin: top left
    private int origin_Y;

    public ExplorationMap(int origin_X, int origin_Y) {
        super();
        this.origin_X = origin_X;
        this.origin_Y = origin_Y;
    }

    public void leftExtend() {
        int[][] map = this.getMap();
        int height = map.length;
        int width = map[0].length;
        int[][] tmp = new int[height][width*2];
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                tmp[i][j+width] = map[i][j];
            }
        }
        this.origin_Y = this.origin_Y + height;
        setMap(tmp);
    }

    public void rightExtend() {
        int[][] map = this.getMap();
        int height = map.length;
        int width = map[0].length;
        int[][] tmp = new int[height][width * 2];
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                tmp[i][j] = map[i][j];
            }
        }
        setMap(tmp);
    }

    public void topExtend() {
        int[][] map = this.getMap();
        int height = map.length;
        int width = map[0].length;
        int[][] tmp = new int[height*2][width];
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                tmp[i+height][j] = map[i][j];
            }
        }
        this.origin_X = this.origin_X + height;
        setMap(tmp);
    }

    public void bottomExtend() {
        int[][] map = this.getMap();
        int height = map.length;
        int width = map[0].length;
        int[][] tmp = new int[height * 2][width];
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                tmp[i][j] = map[i][j];
            }
        }
        setMap(tmp);
    }
}
