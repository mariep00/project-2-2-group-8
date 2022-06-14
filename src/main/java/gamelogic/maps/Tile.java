package gamelogic.maps;

import gui.gamescreen.AgentType;

public class Tile {
    
    public enum Type {
        FLOOR,
        WALL,
        TARGET_AREA,
        DOOR,
        WINDOW,
        TELEPORT_ENTRANCE,
        TELEPORT_EXIT;
    }

    private Type type;
    public boolean shaded;
    public boolean seeThrough;
    private SpecialFeature feature;
    private MarkerInterface[] markers;

    public Tile () {
        this(Type.FLOOR, true);
    }
    public Tile (Type type, boolean seeThrough) {
        this.type = type;
        this.seeThrough = seeThrough;
        shaded = false;
        markers = new MarkerInterface[2];
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

    public boolean isWall() {
        return getType() == Type.WALL;
    }

    public void setSpecialFeature (SpecialFeature feature) {
        this.feature = feature;
    }

    public void setSeeThrough (boolean seeThrough) {
        this.seeThrough = seeThrough;
    }

    public MarkerInterface[] getMarkers() {
        return markers;
    }
    public PheromoneMarker getPheromoneMarker(AgentType agentType) {
        if (agentType == AgentType.GUARD) {
            return markers[0] != null ? (PheromoneMarker) markers[0] : null;
        } else {
            return markers[1] != null ? (PheromoneMarker) markers[1] : null;
        }
    }

    public void removeMarker(MarkerInterface marker, AgentType agentType) {
        if (marker instanceof PheromoneMarker) {
            if(agentType == AgentType.GUARD) {
                markers[0] = null;
            }
            else{
                markers[1] = null;
            }
        }
    }

    public void addMarker (MarkerInterface marker, AgentType agentType) {
        if (marker instanceof PheromoneMarker) {
            if(agentType == AgentType.GUARD) {
                markers[0] = marker;
            }
            else{
                markers[1] = marker;
            }
        }
    }

    public String toString() {
        return "type = " + type + ", shaded = " + shaded;
    }

    public boolean isTeleportEntrance() {
        return getType() == Type.TELEPORT_ENTRANCE;
    }
    public boolean isTeleportExit() { return getType() == Type.TELEPORT_EXIT; }

    public boolean equals(Tile other) { return type == other.type && shaded == other.shaded; }
}
