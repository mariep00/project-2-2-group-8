package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.maps.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class State {
    private final Vector2D[] agentPositions;

    private final List<Vector2D>[] visions;
    private final LinkedList<Tile> tilesWithMarker;
    private final ArrayList<GuardYell> guardGuardYells;

    public State(Vector2D[] agentPositions, List<Vector2D>[] visions, LinkedList<Tile> tilesWithMarker, ArrayList<GuardYell> guardGuardYells) {
        this.agentPositions = agentPositions;
        this.visions = visions;
        this.tilesWithMarker = tilesWithMarker;
        this.guardGuardYells = guardGuardYells;
    }

    public State(Vector2D[] agentPositions, List<Vector2D>[] visions, LinkedList<Tile> tilesWithMarker) {
        this(agentPositions, visions, tilesWithMarker, new ArrayList<>());
    }

    public void addTileWithMarker(Tile tile) { tilesWithMarker.add(tile); }
    public void setAgentPosition(int agentIndex, Vector2D position) { agentPositions[agentIndex] = position; }
    public void setAgentVision(int agentIndex, List<Vector2D> vision) { visions[agentIndex] = vision; }
    public void addGuardYell(GuardYell guardYell) { guardGuardYells.add(guardYell); }

    // *** Actual Tile objects are not being copied! *** Might have to change this later
    public State copyOf() { return new State(agentPositions.clone(), visions.clone(), (LinkedList<Tile>) tilesWithMarker.clone()); }

    public Vector2D[] getAgentPositions() { return agentPositions; }
    public Vector2D getAgentPosition(int agentIndex) { return agentPositions[agentIndex]; }
    public List<Vector2D>[] getVisions() { return visions; }
    public List<Vector2D> getVision(int agentIndex) { return visions[agentIndex]; }
    public LinkedList<Tile> getTilesWithMarker() { return tilesWithMarker; }
    public List<GuardYell> getGuardYells() { return guardGuardYells; }

    public String toString() { return "Agent positions: " + Arrays.toString(agentPositions); }
}
