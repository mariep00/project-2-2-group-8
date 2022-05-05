package gamelogic.agent;

import datastructures.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class AgentsSeen {
    private final Vector2D[] positions;
    private final int[] nrTimeStepsAgo;
    private final int numberOfGuards;

    public AgentsSeen(Vector2D[] positions, int[] nrTimeStepsAgo, int numberOfGuards) {
        this.positions = positions;
        this.nrTimeStepsAgo = nrTimeStepsAgo;
        this.numberOfGuards = numberOfGuards;
    }
    public AgentsSeen(Vector2D[] positions, int numberOfGuards) {
        this.positions = positions;
        this.nrTimeStepsAgo = new int[positions.length];
        this.numberOfGuards = numberOfGuards;
    }

    public int getNumberOfGuards() { return numberOfGuards; }
    public Vector2D[] getPositions() { return positions; }
    public int[] getNrTimeStepsAgo() { return nrTimeStepsAgo; }
    public List<Vector2D> getGuardsCurrentlyInVision() {
        ArrayList<Vector2D> guardsInVision = new ArrayList<>();
        for (int i = 0; i < numberOfGuards; i++) {
            if (nrTimeStepsAgo[i] == 0) guardsInVision.add(positions[i]);
        }
        return guardsInVision;
    }
    public List<Vector2D> getIntrudersCurrentlyInVision() {
        ArrayList<Vector2D> intrudersInVision = new ArrayList<>();
        for (int i = numberOfGuards; i < positions.length; i++) {
            if (nrTimeStepsAgo[i] == 0) intrudersInVision.add(positions[i]);
        }
        return intrudersInVision;
    }
}
