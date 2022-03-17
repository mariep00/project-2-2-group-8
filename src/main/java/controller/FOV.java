package controller;

import controller.maps.VisionMap;

import java.util.ArrayList;

public class FOV {
    
    private double visionAngle;
    private double normalVisionRange;
    private double currentVisionRange;
    private double direction;
    private Vector2D center;

    private double range = 4.0; // How many rows of endpoints should be added. Higher number means slower code because of more rays but also better accuracy. Maybe make dependent on viewRange?
    
    private VisionMap visionMap;
    private VisionMap areaMap;
    private ArrayList<Vector2D> endpoints;

    public FOV (double normalVisionRange) {
        this.normalVisionRange = normalVisionRange;
        visionMap = new VisionMap(normalVisionRange);
        areaMap = new VisionMap(normalVisionRange);
        endpoints = new ArrayList<>();
    }

    /**
     * Calculates the visible tiles for an Agent
     * @param visionAngle angle of the visible field
     * @param newVisionRange how far the agent can see
     * @param areaMap small map of the immediate surroundings of the agent
     * @param direction direction of sight of the agent
     * @return Returns a VisionMap where all tiles that are visible to the agent are marked
     */
    public VisionMap calculate (double visionAngle, double newVisionRange, VisionMap areaMap, double direction) {
        this.visionAngle = visionAngle;
        this.direction = direction;
        this.areaMap = areaMap;
        this.visionMap = new VisionMap(newVisionRange);
        this.endpoints = new ArrayList<>();

        if(newVisionRange!=normalVisionRange) { currentVisionRange = newVisionRange;
        } else { currentVisionRange = normalVisionRange;}

        initiateViewingField();
        rayTracing();
        return visionMap;
    }

    /**
     * Initiates the viewing field of the agent. 
     * Calculates the correct angles and endpoints given the range, direction and vision angle.
     */
    private void initiateViewingField() {
        double[] angles = calculateAngles(direction, visionAngle);
        double[] angles2 = calculateAngles(direction, visionAngle/2);
        this.center = visionMap.getCenter();

        Vector2D p1 = calculatePoint(center, currentVisionRange, angles[0]);
        Vector2D p2 = calculatePoint(center, currentVisionRange, angles2[0]);
        Vector2D p3 = calculatePoint(center, currentVisionRange, direction);
        Vector2D p4 = calculatePoint(center, currentVisionRange, angles2[1]);
        Vector2D p5 = calculatePoint(center, currentVisionRange, angles[1]);
     
        Vector2D[] line1 = calculateLine(p1, p2);
        addToEndpoints(line1);
        
        Vector2D[] line2 = calculateLine(p2, p3);
        addToEndpoints(line2);
        
        Vector2D[] line3 = calculateLine(p3, p4);
        addToEndpoints(line3);
        
        Vector2D[] line4 = calculateLine(p4, p5);
        addToEndpoints(line4);
        
        if (currentVisionRange>range+1.0) {
            Vector2D p6 = calculatePoint(center, currentVisionRange-range, angles[0]);
            p2 = calculatePoint(center, currentVisionRange-range, angles2[0]);
            p3 = calculatePoint(center, currentVisionRange-range, direction);
            p4 = calculatePoint(center, currentVisionRange-range, angles2[1]);
            Vector2D p7 = calculatePoint(center, currentVisionRange-range, angles[1]);

            line1 = calculateLine(p6, p2);
            addToEndpoints(line1);
            
            line2 = calculateLine(p2, p3);
            addToEndpoints(line2);
            
            line3 = calculateLine(p3, p4);
            addToEndpoints(line3);
            
            line4 = calculateLine(p4, p7);
            addToEndpoints(line4);

            Vector2D[] line5 = calculateLine(p6, p1);
            addToEndpoints(line5);

            Vector2D[] line6 = calculateLine(p7, p5);
            addToEndpoints(line6);

            this.endpoints = floodFillEndpoints(calculatePoint(center, currentVisionRange-2.0, direction));
            
        }     
        
    }

    /**
     * Casts rays (lines) to the endpoints and fills in the VisionMap with all visible tiles
     */
    private void rayTracing () { 
        for (int i=0; i<endpoints.size(); i++) {
            Vector2D[] inVision = rayTracingLine(center, endpoints.get(i));
    
            visionMap.insertElement(inVision, 1);
        }     
    }

    /**
     * Single instance of casting a ray
     * @param p0 starting point of the ray
     * @param p1 ending point of the ray
     * @return Returns all visible positions 
     */
    private Vector2D[] rayTracingLine (Vector2D p0, Vector2D p1) {
        ArrayList<Vector2D> line = new ArrayList<Vector2D>();
        int n = calculateDistance(p0, p1);
        boolean noWall = true;
        for (int i=0; noWall&&i<=n; i++) {
            double t = ((double)i)/n;
            Vector2D p = lerpPoint(p0, p1, t);
            if (areaMap.getTile(p.x, p.y)==1 || areaMap.getTile(p.x, p.y)==2) {
                noWall = false;
            }
            if (areaMap.getTile(p.x, p.y)!=2) {
                line.add(p);
            }
        }
        Vector2D[] pointsLine = new Vector2D[line.size()];
        pointsLine = line.toArray(pointsLine);
        return pointsLine;
    }

    /**
     * Calculates a line in a grid between two points
     * @param p0 starting point of the line
     * @param p1 ending point of the line
     * @return Returns an array of points that together represent a line
     */
    public Vector2D[] calculateLine (Vector2D p0, Vector2D p1) {
        ArrayList<Vector2D> line = new ArrayList<Vector2D>();
        final int N = calculateDistance(p0, p1);
        for (int i=0; i<=N; i++) {
            double t = ((double)i)/N;
            line.add(lerpPoint(p0, p1, t));
        }
        Vector2D[] pointsLine = new Vector2D[line.size()];
        pointsLine = line.toArray(pointsLine);
        return pointsLine;
    }

    /**
     * Linear interpolation
     * @param start
     * @param end
     * @param t
     * @return
     */
    private double lerp (int start, int end, double t) {
        return (start+t*(end-start));
    }

    /**
     * Linear interpolates two points
     * @param p0
     * @param p1
     * @param t
     * @return
     */
    private Vector2D lerpPoint (Vector2D p0, Vector2D p1, double t) {
        return new Vector2D((int)Math.round(lerp(p0.x, p1.x, t)), (int)Math.round(lerp(p0.y, p1.y, t)));
    }

    private int calculateDistance(Vector2D center, Vector2D other) {
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

    private ArrayList<Vector2D> floodFillEndpoints (Vector2D start) {
        VisionMap tmp = new VisionMap(currentVisionRange);
        tmp.insertElement(endpoints, 1);
        ArrayList<Vector2D> frontiers = new ArrayList<>();
        frontiers.add(start);
        tmp.setTile(start.x, start.y, 1);
        while (frontiers.size()>0) {
            for (int i=0; i<frontiers.size(); i++) {
                Vector2D[] adj = getNeighbours(frontiers.get(i));
                for (int j=0;j<adj.length;j++) {
                    if (tmp.getTile(adj[j].x, adj[j].y) == 0) {
                        tmp.setTile(adj[j].x, adj[j].y, 1);
                        frontiers.add(adj[j]);
                    }
                }
                frontiers.remove(i);
            }
        }
        return tmp.getInVisionAbsolute();
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

    private void addToEndpoints (Vector2D[] line) {
        for (int i=0; i < line.length; i++) {
            endpoints.add(line[i]);
        }
    }

}
