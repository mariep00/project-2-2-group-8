package gamelogic.controller;

import gamelogic.Vector2D;
import gamelogic.maps.VisionMap;

import java.util.ArrayList;

public class VisionController {

    private static double range = 4.0; // How many rows of endpoints should be added. Higher number means slower code because of more rays but also better accuracy. Maybe make dependent on viewRange?

    /**
     * Calculates the visible tiles for an Agent
     * @param visionAngle angle of the visible field
     * @param newVisionRange how far the agent can see
     * @param areaMap small map of the immediate surroundings of the agent
     * @param direction direction of sight of the agent
     * @return Returns a VisionMap where all tiles that are visible to the agent are marked
     */
    public static VisionMap calculateVision (double visionAngle, double visionRange, VisionMap areaMap, double direction) {
        VisionMap visionMap = new VisionMap(visionRange);

        ArrayList<Vector2D> endpoints = initiateEndpoints(direction, visionAngle, visionRange, visionMap.getCenter());
        visionMap = rayTracing(visionMap, endpoints, areaMap);
        return visionMap;
    }

    /**
     * Initiates the viewing field of the agent. 
     * Calculates the correct angles and endpoints given the range, direction and vision angle.
     * @param center
     * @param visionRange
     * @param visionAngle
     * @param direction
     */
    private static ArrayList<Vector2D> initiateEndpoints(double direction, double visionAngle, double visionRange, Vector2D center) {
        ArrayList<Vector2D> endpoints = new ArrayList<>();
        double[] angles = calculateAngles(direction, visionAngle);
        double[] angles2 = calculateAngles(direction, visionAngle/2);

        Vector2D p1 = calculatePoint(center, visionRange, angles[0]);
        Vector2D p2 = calculatePoint(center, visionRange, angles2[0]);
        Vector2D p3 = calculatePoint(center, visionRange, direction);
        Vector2D p4 = calculatePoint(center, visionRange, angles2[1]);
        Vector2D p5 = calculatePoint(center, visionRange, angles[1]);
     
        Vector2D[] line1 = calculateLine(p1, p2);
        endpoints = addArrayToList(endpoints, line1);
        
        Vector2D[] line2 = calculateLine(p2, p3);
        endpoints = addArrayToList(endpoints, line2);
        
        Vector2D[] line3 = calculateLine(p3, p4);
        endpoints = addArrayToList(endpoints, line3);
        
        Vector2D[] line4 = calculateLine(p4, p5);
        endpoints = addArrayToList(endpoints, line4);
        
        if (visionRange>range+1.0) {
            Vector2D p6 = calculatePoint(center, visionRange-range, angles[0]);
            p2 = calculatePoint(center, visionRange-range, angles2[0]);
            p3 = calculatePoint(center, visionRange-range, direction);
            p4 = calculatePoint(center, visionRange-range, angles2[1]);
            Vector2D p7 = calculatePoint(center, visionRange-range, angles[1]);

            line1 = calculateLine(p6, p2);
            endpoints = addArrayToList(endpoints, line1);
            
            line2 = calculateLine(p2, p3);
            endpoints = addArrayToList(endpoints, line2);
            
            line3 = calculateLine(p3, p4);
            endpoints = addArrayToList(endpoints, line3);
            
            line4 = calculateLine(p4, p7);
            endpoints = addArrayToList(endpoints, line4);

            Vector2D[] line5 = calculateLine(p6, p1);
            endpoints = addArrayToList(endpoints, line5);

            Vector2D[] line6 = calculateLine(p7, p5);
            endpoints = addArrayToList(endpoints, line6);

            endpoints = floodFillEndpoints(calculatePoint(center, visionRange-2.0, direction), endpoints, visionRange);
            
        }   
        return endpoints;  
        
    }

    /**
     * Casts rays (lines) to the endpoints and fills in the VisionMap with all visible tiles
     * @param endpoints
     * @param visionMap
     * @param areaMap
     * @return 
     */
    private static VisionMap rayTracing (VisionMap visionMap, ArrayList<Vector2D> endpoints, VisionMap areaMap) { 
        Vector2D center = visionMap.getCenter();
        for (int i=0; i<endpoints.size(); i++) {
            Vector2D[] inVision = rayTracingLine(center, endpoints.get(i), areaMap);
    
            visionMap.insertElement(inVision, 1);
        }     
        return visionMap;
    }

    /**
     * Single instance of casting a ray
     * @param p0 starting point of the ray
     * @param p1 ending point of the ray
     * @param areaMap
     * @return Returns all visible positions 
     */
    private static Vector2D[] rayTracingLine (Vector2D p0, Vector2D p1, VisionMap areaMap) {
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
    public static Vector2D[] calculateLine (Vector2D p0, Vector2D p1) {
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
    private static double lerp (int start, int end, double t) {
        return (start+t*(end-start));
    }

    /**
     * Linear interpolates two points
     * @param p0
     * @param p1
     * @param t
     * @return
     */
    private static Vector2D lerpPoint (Vector2D p0, Vector2D p1, double t) {
        return new Vector2D((int)Math.round(lerp(p0.x, p1.x, t)), (int)Math.round(lerp(p0.y, p1.y, t)));
    }

    private static int calculateDistance(Vector2D center, Vector2D other) {
        int dx = other.x - center.x;
        int dy = other.y - center.y;
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    private static Vector2D calculatePoint (Vector2D center, double distance, double angle) {
        double angleRad = Math.toRadians(angle);
        int x = (int)Math.round(center.x+(distance*Math.cos(angleRad)));
        int y = (int)Math.round(center.y+(distance*Math.sin(angleRad)));

        return new Vector2D(x, y);
    }

    private static double[] calculateAngles (double midAngle, double betwAngle) {
        double[] results = new double[2];
        results[0] = checkAngle(midAngle-(0.5*betwAngle));
        results[1] = checkAngle(midAngle+(0.5*betwAngle));

        return results;
    }

    private static double checkAngle (double angle) {
        double corrAngle = angle;
        if (angle<0) {
            corrAngle = angle + 360.0;
        } else if (angle>360.0) {
            corrAngle = angle - 360.0;
        }
        return corrAngle;
    }

    private static ArrayList<Vector2D> floodFillEndpoints (Vector2D start, ArrayList<Vector2D> endpoints, double visionRange) {
        VisionMap tmp = new VisionMap(visionRange);
        tmp.insertElement(endpoints, 1);
        ArrayList<Vector2D> frontiers = new ArrayList<>();
        frontiers.add(start);
        tmp.setTile(start.x, start.y, 1);
        while (frontiers.size()>0) {
            for (int i=0; i<frontiers.size(); i++) {
                Vector2D[] adj = frontiers.get(i).getNeighbours();
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

    //TODO Could be a generally helpful method (maybe create collection of those)
    private static ArrayList<Vector2D> addArrayToList (ArrayList<Vector2D> list, Vector2D[] array) {
        for (int i=0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

}
