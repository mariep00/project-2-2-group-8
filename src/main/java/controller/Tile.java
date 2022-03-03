package controller;

public class Tile {
    
    public enum Type {
        FLOOR,
        WALL,
        TARGET_AREA,
        SPAWN_AREA_GUARDS,
        DOOR,
        WINDOW,
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

    public boolean isShaded() { return shaded; }

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

    public String toString() {
        return "type = " + type + ", shaded = " + shaded;
    }
}
