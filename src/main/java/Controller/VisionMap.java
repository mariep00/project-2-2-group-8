package Controller;

import java.util.ArrayList;

public class VisionMap{
    
    private Vector2D center;
    private int[][] mapGrid;

    private int width;
    private int height;

    public VisionMap (double visionRange) {
        int radius = (int)Math.round(visionRange);
        
        int diameter = (2*(radius))+1;
        createMap(diameter, diameter, 1.0f);

        center = new Vector2D(radius, radius);
    }

    public Vector2D getCenter() {
            return center;
    }

    public Vector2D[] getInVision() {
        ArrayList<Vector2D> vectors = new ArrayList<>();
        for (int i=0; i<mapGrid.length; i++) {
            for (int j=0; j<mapGrid[i].length; j++) {
                if (mapGrid[i][j] == 1) {
                    Vector2D tmp = new Vector2D(j, i);
                    vectors.add(tmp);
                }
            }
        }
        Vector2D[] inVision = new Vector2D[vectors.size()];
        inVision = vectors.toArray(inVision);
        return inVision;

    }

    //@Override
    public int[][] getMap() {
        return mapGrid;
    }

    //@Override
    public void setMap(int[][] map) {    
        mapGrid = map;
        
    }

    //@Override
    public int getTile(int x, int y) {
        return mapGrid[y][x];
    }

    //@Override
    public void setTile(int x, int y, int tile) {
        mapGrid[y][x] = tile;
        
    }

    //@Override
    public void createMap(int width, int height, float scaling) {
        this.width = width+1;
        this.height = height+1;
        mapGrid = new int[this.height][this.width];
        
    }

    //@Override
    public void insertElement(int x1, int y1, int x2, int y2, int tile) {
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                mapGrid[j][i] = tile;
            }
        }
        
    }

    //@Override
    public void insertElement(Vector2D[] points, int tile) {
        for (int i=0; i<points.length; i++) {
            mapGrid[points[i].getY()][points[i].getX()] = tile;
        }
        
    }

    public Vector2D translateCoordinateByCenter (Vector2D p) {
        return p.subtract(center);
    }

}
