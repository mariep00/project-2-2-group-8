package gamelogic.maps;

import gamelogic.Vector2D;

import java.util.ArrayList;

public class VisionMap {
    
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

    public ArrayList<Vector2D> getInVision() {
        ArrayList<Vector2D> vectors = new ArrayList<>();
        for (int i=0; i<mapGrid.length; i++) {
            for (int j=0; j<mapGrid[i].length; j++) {
                if (mapGrid[i][j] == 1) {
                    Vector2D tmp = new Vector2D(j-getCenter().x, i-getCenter().y); // Relative to center
                    vectors.add(tmp);
                }
            }
        }
        return vectors;
    }

    public ArrayList<Vector2D> getInVisionAbsolute() {
        ArrayList<Vector2D> vectors = new ArrayList<>();
        for (int i=0; i<mapGrid.length; i++) {
            for (int j=0; j<mapGrid[i].length; j++) {
                if (mapGrid[i][j] == 1) {
                    Vector2D tmp = new Vector2D(j, i); // Relative to center
                    vectors.add(tmp);
                }
            }
        }
        return vectors;
    }

    public int[][] getMap() {
        return mapGrid;
    }

    public void setMap(int[][] map) {    
        mapGrid = map;
        
    }

    public int getTile(int x, int y) {
        return mapGrid[y][x];
    }

    public void setTile(int x, int y, int tile) {
        mapGrid[y][x] = tile;
        
    }

    public void createMap(int width, int height, float scaling) {
        this.width = width;
        this.height = height;
        mapGrid = new int[this.height][this.width];
        
    }

    public void insertElement(Vector2D point, int tile) {
    
        mapGrid[point.y][point.x] = tile;
        
    }

    public void insertElement(Vector2D[] points, int tile) {
        for (int i=0; i<points.length; i++) {
            mapGrid[points[i].y][points[i].x] = tile;
        }
        
    }

    public void insertElement(ArrayList<Vector2D> points, int tile) {
        for (Vector2D point : points) {
            mapGrid[point.y][point.x] = tile;
        }
        
    }

    public Vector2D translateCoordinateByCenter (Vector2D p) {
        return p.subtract(center);
    }

}
