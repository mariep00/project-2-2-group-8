
public class Tile {
    
    public enum Type {
        FLOOR(0, false), WALL(0, false), SPAWNAREAINTRUDERS(0, false),
        SPAWNAREAGUARDS(0, false), TARGETAREA(0, false), DOOR(0, false), WINDOW(0, false), 
        TELEPORT(0, false);

        int index;
        boolean shaded;

        Type (int index, boolean shaded) {
            this.index = index;
            this.shaded = shaded;
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
