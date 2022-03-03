package Controller;

public class
Tile {
    
    public enum Type {
        FLOOR, WALL, SPAWN_AREA_INTRUDERS,
        SPAWN_AREA_GUARDS, TARGET_AREA, DOOR, WINDOW, 
        TELEPORT;
    }

    private Type type;
    public boolean shaded;
    public boolean seeThrough;
    private SpecialFeature feature;

    public Tile () {
            type = Type.FLOOR;
            seeThrough = true;
            shaded = false;
    }

    public Type getType() {
        return this.type;
    }

    public SpecialFeature getFeature() {
        return feature;
    }

    public void setShaded(boolean shaded) {
        this.shaded = shaded;
    }

    public void setType (Type type) {
        this.type = type;
        if (type == Type.WALL || type == Type.DOOR) {
            seeThrough = false;
        }
    }

    public void setSpecialFeature (SpecialFeature feature) {
        this.feature = feature;
    }

    public void setSeeThrough (boolean seeThrough) {
        this.seeThrough = seeThrough;
    }

}
