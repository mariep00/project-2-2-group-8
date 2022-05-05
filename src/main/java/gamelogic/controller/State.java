package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.agent.AgentsSeen;
import gamelogic.datacarriers.GuardYell;
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
    private final AgentsSeen[] agentsSeen;

    public State(Vector2D[] agentPositions, List<Vector2D>[] visions, LinkedList<Tile> tilesWithMarker,
                 ArrayList<GuardYell> guardGuardYells, AgentsSeen[] agentsSeen) {
        this.agentPositions = agentPositions;
        this.visions = visions;
        this.tilesWithMarker = tilesWithMarker;
        this.guardGuardYells = guardGuardYells;
        this.agentsSeen = agentsSeen;
    }

    public State(Vector2D[] agentPositions, List<Vector2D>[] visions, LinkedList<Tile> tilesWithMarker, AgentsSeen[] agentsSeen) {
        this(agentPositions, visions, tilesWithMarker, new ArrayList<>(), agentsSeen);
    }

    public void addTileWithMarker(Tile tile) { tilesWithMarker.add(tile); }
    public void setAgentPosition(int agentIndex, Vector2D position) { agentPositions[agentIndex] = position; }
    public void setAgentVision(int agentIndex, List<Vector2D> vision) { visions[agentIndex] = vision; }
    public void addGuardYell(GuardYell guardYell) { guardGuardYells.add(guardYell); }
    public void setAgentsSeen(int agentIndex, AgentsSeen agentsSeen) {
        this.agentsSeen[agentIndex] = agentsSeen;
    }

    public State copyOf() {
        AgentsSeen[] agentsSeensNew = new AgentsSeen[agentsSeen.length];
        for (int i = 0; i < agentsSeensNew.length; i++) {
            agentsSeensNew[i] = new AgentsSeen(agentsSeen[i].getPositions().clone(), agentsSeen[i].getNrTimeStepsAgo().clone(), agentsSeen[i].getNumberOfGuards());
        }
        return new State(agentPositions.clone(), visions.clone(), (LinkedList<Tile>) tilesWithMarker.clone(), agentsSeensNew);
    }

    public Vector2D[] getAgentPositions() { return agentPositions; }
    public Vector2D getAgentPosition(int agentIndex) { return agentPositions[agentIndex]; }

    public List<Vector2D>[] getVisions() { return visions; }
    public List<Vector2D> getVision(int agentIndex) { return visions[agentIndex]; }
    public LinkedList<Tile> getTilesWithMarker() { return tilesWithMarker; }
    public List<GuardYell> getGuardYells() { return guardGuardYells; }
    public AgentsSeen getAgentsSeen(int agentIndex) { return agentsSeen[agentIndex]; }

    public String toString() { return "Agent positions: " + Arrays.toString(agentPositions); }
}
