import java.util.ArrayList;

public class VisionMap extends Map{
    
    private Vector2D center;

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

}
