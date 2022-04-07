package gamelogic.controller;

import gamelogic.Vector2D;
import gamelogic.maps.EndingExploration;
import gamelogic.maps.Tile;

import java.util.ArrayList;
import java.util.LinkedList;

public class State {
    private final Vector2D[] agentPositions;

    private final ArrayList<Vector2D>[] visions;
    private final LinkedList<Tile> tilesWithMarker;
    private final EndingExploration endingExploration;

    public State(Vector2D[] agentPositions, ArrayList<Vector2D>[] visions, LinkedList<Tile> tilesWithMarker, EndingExploration endingExploration) {
        this.agentPositions = agentPositions;
        this.visions = visions;
        this.tilesWithMarker = tilesWithMarker;
        this.endingExploration = endingExploration;
    }

    public void updateVision(int agentIndex, ArrayList<Vector2D> vision) {
        visions[agentIndex] = vision;
    }

    public State copyOf() {
        return new State(agentPositions, visions, tilesWithMarker, endingExploration);
    }
}
