package controller;

import java.util.ArrayList;

import controller.maps.VisionMap;

public class FOV {
    
    private double visionAngle;
    private double normalVisionRange;
    private double currentVisionRange;
    private double direction;
    private Vector2D center;
    
    private VisionMap visionGrid;
    private VisionMap areaMap;
    private ArrayList<Vector2D> endpoints;

    public FOV (double normalVisionRange) {
        this.normalVisionRange = normalVisionRange;
        visionGrid = new VisionMap(normalVisionRange);
        areaMap = new VisionMap(normalVisionRange);
        endpoints = new ArrayList<>();
    }

    public int[][] getMap () {
        return visionGrid.getMap();
    }

    public void calculate (double visionAngle, double newVisionRange, VisionMap areaMap, double direction) {
        this.visionAngle = visionAngle;
        this.direction = direction;
        this.areaMap = areaMap;

        if(newVisionRange!=normalVisionRange) { currentVisionRange = newVisionRange;
        } else { currentVisionRange = normalVisionRange;}

        //Mark all Tiles that could be visible for the Agent
        //Marked as 1 in the VisionMap
        initiateViewingField();
        rayTracing();
    
    }

    private void initiateViewingField() {
        double[] angles = calculateAngles(direction, visionAngle);
        double[] angles2 = calculateAngles(direction, visionAngle/2);
        this.center = visionGrid.getCenter();

        Vector2D p1 = calculatePoint(center, currentVisionRange, angles[0]);
        Vector2D p2 = calculatePoint(center, currentVisionRange, angles2[0]);
        Vector2D p3 = calculatePoint(center, currentVisionRange, direction);
        Vector2D p4 = calculatePoint(center, currentVisionRange, angles2[1]);
        Vector2D p5 = calculatePoint(center, currentVisionRange, angles[1]);
/*
        Vector2D[] line1 = calculateLine(center, p1);
        visionGrid.insertElement(line1, 1);
        Vector2D[] line2 = calculateLine(center, p5);
        visionGrid.insertElement(line2, 1);
*/
        Vector2D[] line3 = calculateLine(p1, p2);
        addToEndpoints(line3);
        //visionGrid.insertElement(line3, 1);
        Vector2D[] line4 = calculateLine(p2, p3);
        addToEndpoints(line4);
        //visionGrid.insertElement(line4, 1);
        Vector2D[] line5 = calculateLine(p3, p4);
        addToEndpoints(line5);
        //visionGrid.insertElement(line5, 1);
        Vector2D[] line6 = calculateLine(p4, p5);
        addToEndpoints(line6);
        //visionGrid.insertElement(line6, 1);

        //floodFill(lerpPoint(center, p2, 0.5));

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
        return new Vector2D((int)Math.round(lerp(p0.x, p1.x, t)), (int)Math.round(lerp(p0.y, p1.y, t)));
    }

    private int calcualteDistance(Vector2D center, Vector2D other) {
        int dx = other.x - center.x;
        int dy = other.y - center.y;
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    private Vector2D calculatePoint (Vector2D center, double distance, double angle) {
        double angleRad = Math.toRadians(angle);
        int x = (int)Math.round(center.x+(distance*Math.cos(angleRad)));
        int y = (int)Math.round(center.y+(distance*Math.sin(angleRad)));

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
        visionGrid.setTile(start.x, start.y, 1);
        while (frontiers.size()>0) {
            for (int i=0; i<frontiers.size(); i++) {
                Vector2D[] adj = getNeighbours(frontiers.get(i));
                for (int j=0;j<adj.length;j++) {
                    if (visionGrid.getTile(adj[j].x, adj[j].y) == 0) {
                        visionGrid.setTile(adj[j].x, adj[j].y, 1);
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
        neighbours[0] = new Vector2D(center.x, center.y-1);
        //east
        neighbours[1] = new Vector2D(center.x+1, center.y);
        //south
        neighbours[2] = new Vector2D(center.x, center.y+1);
        //west
        neighbours[3] = new Vector2D(center.x-1, center.y);

        return neighbours;
    }

    private void rayTracing () {
        
        for (int i=0; i<endpoints.size(); i++) {
            Vector2D[] inVision = rayTracingLine(center, endpoints.get(i));
            visionGrid.insertElement(inVision, 1);
        }
        
    }

    private Vector2D[] rayTracingLine (Vector2D p0, Vector2D p1) {
        ArrayList<Vector2D> line = new ArrayList<Vector2D>();
        int n = calcualteDistance(p0, p1);
        boolean noWall = true;
        for (int i=0; noWall&&i<=n; i++) {
            double t = ((double)i)/n;
            Vector2D p = lerpPoint(p0, p1, t);
            if (hitWall(p)) {
                noWall = false;
            }
            line.add(p);

        }
        Vector2D[] pointsLine = new Vector2D[line.size()];
        pointsLine = line.toArray(pointsLine);
        return pointsLine;
    }

    private boolean hitWall(Vector2D p) {
        boolean wall = areaMap.getTile(p.x, p.y)==1;
        return wall;
    }  

    private void addToEndpoints (Vector2D[] line) {
        for (int i=0; i < line.length; i++) {
            endpoints.add(line[i]);
        }
    }

}
