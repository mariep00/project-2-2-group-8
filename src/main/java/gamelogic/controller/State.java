package gamelogic.controller;

import gamelogic.Vector2D;
import gamelogic.maps.Tile;

import java.util.ArrayList;
import java.util.LinkedList;

public class State {
    private final Vector2D[] agentPositions;

    private final ArrayList<Vector2D>[] visions;
    private final LinkedList<Tile> tilesWithMarker;

    public State(Vector2D[] agentPositions, ArrayList<Vector2D>[] visions, LinkedList<Tile> tilesWithMarker) {
        this.agentPositions = agentPositions;
        this.visions = visions;
        this.tilesWithMarker = tilesWithMarker;
    }

    public void updateVision(int agentIndex, ArrayList<Vector2D> vision) { visions[agentIndex] = vision; }
    public void addTileWithMarker(Tile tile) { tilesWithMarker.add(tile); }
    public void setAgentPosition(int agentIndex, Vector2D position) { agentPositions[agentIndex] = position; }
    public void setAgentVision(int agentIndex, ArrayList<Vector2D> vision) { visions[agentIndex] = vision; }

    // *** Actual Tile objects, and the EndingExploration object are not being copied! *** Might have to change this later
    public State copyOf() { return new State(agentPositions, visions.clone(), (LinkedList<Tile>) tilesWithMarker.clone()); }

    public Vector2D[] getAgentPositions() { return agentPositions; }
    public Vector2D getAgentPosition(int agentIndex) { return agentPositions[agentIndex]; }
    public ArrayList<Vector2D>[] getVisions() { return visions; }
    public ArrayList<Vector2D> getVision(int agentIndex) { return visions[agentIndex]; }
    public LinkedList<Tile> getTilesWithMarker() { return tilesWithMarker; }
}
