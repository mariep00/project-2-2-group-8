public class VisionMap extends Map{
    
    private Vector2D center;

    public VisionMap (double visionRange) {
        int radius = (int)Math.round(visionRange);
        System.out.println("radius = " + radius);
        
        int diameter = (2*(radius))+1;
        System.out.println("diameter = " + diameter);
        createMap(diameter, diameter, 1.0f);

        center = new Vector2D(radius, radius);

        System.out.println("center(" + center.getX() + ", " + center.getY() + ")");
    }

    public Vector2D getCenter() {
            return center;
    }
}
