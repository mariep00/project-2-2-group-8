package controller;

public class Tile {
    public enum Type {
        FLOOR,
        WALL,
        TARGET_AREA,
        DOOR,
        WINDOW,
        TELEPORT;
    }

    private Type type;
    private boolean shaded;
    private SpecialFeature feature;

    public Tile () {
            type = Type.FLOOR;
    }

    public Tile (Type type) {
        this.type = type;
    }

    public Tile (Type type, SpecialFeature feature) {
        this.type = type;
        this.feature = feature;
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
    }

    public void setSpecialFeature (SpecialFeature feature) {
        this.feature = feature;
    }

    public String toString() {
        return "type = " + type + ", shaded = " + shaded;
    }
}
