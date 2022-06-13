package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.datacarriers.SoundOrigin;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.datacarriers.GuardYell;
import gamelogic.maps.Tile;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")

public class State implements Encodable {
    private final Vector2D[] agentPositions;
    private final List<Vector2D>[] visions;
    private final LinkedList<Tile> tilesWithMarkerGuard;
    private final LinkedList<Tile> tilesWithMarkerIntruder;
    private final ArrayList<GuardYell> guardYells;
    private final VisionMemory[][] agentsSeen;
    private final ArrayList<SoundOrigin> soundOrigins;

    public State(Vector2D[] agentPositions, List<Vector2D>[] visions, LinkedList<Tile> tilesWithMarkerGuard, LinkedList<Tile> tilesWithMarkerIntruder,
                 ArrayList<GuardYell> guardYells, VisionMemory[][] agentsSeen, ArrayList<SoundOrigin> soundOrigins) {
        this.agentPositions = agentPositions;
        this.visions = visions;
        this.tilesWithMarkerGuard = tilesWithMarkerGuard;
        this.tilesWithMarkerIntruder = tilesWithMarkerIntruder;
        this.guardYells = guardYells;
        this.agentsSeen = agentsSeen;
        this.soundOrigins = soundOrigins;
    }

    public State(Vector2D[] agentPositions, List<Vector2D>[] visions, LinkedList<Tile> tilesWithMarkerGuard, VisionMemory[][] agentsSeen) {
        this(agentPositions, visions, tilesWithMarkerGuard, null, new ArrayList<>(), agentsSeen, new ArrayList<>());
    }

    public State(Vector2D[] agentPositions, List<Vector2D>[] visions, LinkedList<Tile> tilesWithMarkerGuard, LinkedList<Tile> tilesWithMarkerIntruder,  VisionMemory[][] agentsSeen) {
        this(agentPositions, visions, tilesWithMarkerGuard, tilesWithMarkerIntruder, new ArrayList<>(), agentsSeen, new ArrayList<>());
    }

    public void addTileWithMarkerGuard(Tile tile) { tilesWithMarkerGuard.add(tile); }
    public void addTileWithMarkerIntruder(Tile tile) { tilesWithMarkerIntruder.add(tile); }
    public void setAgentPosition(int agentIndex, Vector2D position) { agentPositions[agentIndex] = position; }
    public void setAgentVision(int agentIndex, List<Vector2D> vision) { visions[agentIndex] = vision; }
    public void addGuardYell(GuardYell guardYell) { guardYells.add(guardYell); }
    public void setAgentsSeen(int agentIndex, VisionMemory[] agentsSeen) { this.agentsSeen[agentIndex] = agentsSeen; }
    public void addSoundOrigin(SoundOrigin soundOrigin) { soundOrigins.add(soundOrigin); }

    public State copyOf() {
        return new State(agentPositions.clone(), visions.clone(), (LinkedList<Tile>) tilesWithMarkerGuard.clone(), agentsSeen.clone());
    }

    public Vector2D[] getAgentPositions() { return agentPositions; }
    public Vector2D getAgentPosition(int agentIndex) { return agentPositions[agentIndex]; }

    public List<Vector2D>[] getVisions() { return visions; }
    public List<Vector2D> getVision(int agentIndex) { return visions[agentIndex]; }
    public LinkedList<Tile> getTilesWithMarkerGuard() { return tilesWithMarkerGuard; }
    public LinkedList<Tile> getTilesWithMarkerIntruder() { return tilesWithMarkerGuard; }
    public List<GuardYell> getGuardYells() { return guardYells; }
    public VisionMemory[] getAgentsSeen(int agentIndex) { return agentsSeen[agentIndex]; }
    public List<SoundOrigin> getSoundOrigins() { return soundOrigins; }

    public String toString() { return "Agent positions: " + Arrays.toString(agentPositions); }

    @Override
    public double[] toArray() {
        return new double[0];
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return null;
    }

    @Override
    public Encodable dup() {
        return null;
    }
}
