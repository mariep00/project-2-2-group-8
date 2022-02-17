import java.util.ArrayList;

public class FOV {
    
    private double visionAngle;
    private double normalVisionRange;
    private double currentVisionRange;
    private double direction;
    private Vector2D position;

    private VisionMap visionGrid;
    private int[][] areaMap;

    public FOV (double normalVisionRange) {
        this.normalVisionRange = normalVisionRange;
        visionGrid = new VisionMap(normalVisionRange);
    }

    public int[][] getMap () {
        return visionGrid.getMap();
    }

    public void calculate (double visionAngle, double newVisionRange, int[][] areaMap, double direction) {
        this.visionAngle = visionAngle;
        this.direction = direction;
        this.areaMap = areaMap;
        if(newVisionRange!=normalVisionRange) { currentVisionRange = newVisionRange;
        } else { currentVisionRange = normalVisionRange;}

        //Mark all Tiles that could be visible for the Agent
        //Marked as 1 in the VisionMap
        initiateViewingField();
    }

    private void initiateViewingField() {
        double[] angles = calculateAngles(direction, visionAngle);
        double[] angles2 = calculateAngles(direction, visionAngle/2);
        Vector2D center = visionGrid.getCenter();

        Vector2D p1 = calculatePoint(center, visionRange, angles[0]);
        Vector2D p2 = calculatePoint(center, visionRange, angles2[0]);
        Vector2D p3 = calculatePoint(center, visionRange, direction);
        Vector2D p4 = calculatePoint(center, visionRange, angles2[1]);
        Vector2D p5 = calculatePoint(center, visionRange, angles[1]);

        Vector2D[] line1 = calculateLine(center, p1);
        visionGrid.insertElement(line1, 1);
        Vector2D[] line2 = calculateLine(center, p5);
        visionGrid.insertElement(line2, 1);
        Vector2D[] line3 = calculateLine(p1, p2);
        visionGrid.insertElement(line3, 1);
        Vector2D[] line4 = calculateLine(p2, p3);
        visionGrid.insertElement(line4, 1);
        Vector2D[] line5 = calculateLine(p3, p4);
        visionGrid.insertElement(line5, 1);
        Vector2D[] line6 = calculateLine(p4, p5);
        visionGrid.insertElement(line6, 1);

        floodFill(lerpPoint(center, p2, 0.5));

    }

    private Vector2D[] calculateLine (Vector2D p0, Vector2D p1) {
        ArrayList<Vector2D> line = new ArrayList<Vector2D>();
        final int N = calcualteDistance(p0, p1);
        for (int i=0; i<=N; i++) {
            double t = ((double)i)/N;
            line.add(lerpPoint(p0, p1, t));
        }
        Vector2D[] pointsLine = new Vector2D[line.size()];
        pointsLine = line.toArray(pointsLine);
        return pointsLine;
    }

    private double lerp (int start, int end, double t) {
        return (start+t*(end-start));
    }

    private Vector2D lerpPoint (Vector2D p0, Vector2D p1, double t) {
        return new Vector2D((int)Math.round(lerp(p0.getX(), p1.getX(), t)), (int)Math.round(lerp(p0.getY(), p1.getY(), t)));
    }

    private int calcualteDistance(Vector2D center, Vector2D other) {
        int dx = other.getX() - center.getX();
        int dy = other.getY() - center.getY();
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    private Vector2D calculatePoint (Vector2D center, double distance, double angle) {
        double angleRad = Math.toRadians(angle);
        int x = (int)Math.round(center.getX()+(distance*Math.cos(angleRad)));
        int y = (int)Math.round(center.getY()+(distance*Math.sin(angleRad)));

        return new Vector2D(x, y);
    }

    private double[] calculateAngles (double midAngle, double betwAngle) {
        double[] results = new double[2];

        results[0] = checkAngle(midAngle-(0.5*betwAngle));
        results[1] = checkAngle(midAngle+(0.5*betwAngle));

        return results;
    }

    private double checkAngle (double angle) {

        double corrAngle = angle;
        if (angle<0) {
            corrAngle = angle + 360.0;
        } else if (angle>360.0) {
            corrAngle = angle - 360.0;
        }
        return corrAngle;
    }

    private void floodFill (Vector2D start) {

        ArrayList<Vector2D> frontiers = new ArrayList<>();
        frontiers.add(start);
        visionGrid.setTile(start.getX(), start.getY(), 1);
        while (frontiers.size()>0) {
            for (int i=0; i<frontiers.size(); i++) {
                Vector2D[] adj = getNeighbours(frontiers.get(i));
                for (int j=0;j<adj.length;j++) {
                    if (visionGrid.getTile(adj[j].getX(), adj[j].getY()) == 0) {
                        visionGrid.setTile(adj[j].getX(), adj[j].getY(), 1);
                        frontiers.add(adj[j]);
                    }
                }
                frontiers.remove(i);
            }
        }

    }

    private Vector2D[] getNeighbours (Vector2D center) {
        Vector2D[] neighbours = new Vector2D[4];

        //north
        neighbours[0] = new Vector2D(center.getX(), center.getY()-1);
        //east
        neighbours[1] = new Vector2D(center.getX()+1, center.getY());
        //south
        neighbours[2] = new Vector2D(center.getX(), center.getY()+1);
        //west
        neighbours[3] = new Vector2D(center.getX()-1, center.getY());

        return neighbours;
    }

    private void rayTracing (Vector2D[] endpoints) {
        
        boolean finished = false;
        while (!finished) {
            for (int i=0; i<endpoints.length; i++) {

            }
        }
    }

    private Vector2D[] rayTracingLine (Vector2D p0, Vector2D p1) {
        ArrayList<Vector2D> line = new ArrayList<Vector2D>();
        int n = calcualteDistance(p0, p1);
        for (int i=0; hitWall()||i<=n; i++) {
            double t = ((double)i)/n;
            line.add(lerpPoint(p0, p1, t));
        }
        Vector2D[] pointsLine = new Vector2D[line.size()];
        pointsLine = line.toArray(pointsLine);
        return pointsLine;
    }
/*
    private double[] getAnglesRT (double[] sides) {
        if (sides[0]<sides[1]) {

        }
    }
*/

    private boolean hitWall() {
        return false;
    }  
}
