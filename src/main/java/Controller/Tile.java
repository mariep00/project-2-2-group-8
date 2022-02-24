package Controller;

public class Tile {
    public enum Type {
        FLOOR(0), WALL(0), SPAWN_AREA_INTRUDERS(0),
        SPAWN_AREA_GUARDS(0), TARGET_AREA(0), DOOR(0), WINDOW(0),
        TELEPORT(0);

        int index;
        boolean shaded;

        Type (int index, boolean shaded) {
            this.index = index;
            this.shaded = shaded;
        }

        Type(int index) {
            this(index, false);
        }

        public int getIndex () {
            return this.index;
        }

        public boolean isShaded () {
            return this.shaded;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setShaded (boolean shaded) {this.shaded = shaded;}
    }

    private final Type type;

    public Tile () {
            type = Type.FLOOR;
    }

    public Tile (Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public void setIndex(int index) {
        this.type.setIndex(index);
    }

    public void setShaded(boolean shaded) {
        this.type.setShaded(shaded);
    }

}
