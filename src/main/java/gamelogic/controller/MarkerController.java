package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.maps.MarkerInterface;
import gamelogic.maps.PheromoneMarker;
import gamelogic.maps.Tile;
import gui.gamescreen.AgentType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// Might split this controller into a controller for each marker separately. Depends on what markers we want to implement
public class MarkerController {
    private final Controller controller;
    private final int pheromoneMaxSmellingDistance;
    private final double pheromoneReduction;

    public MarkerController(Controller controller) {
        this.controller = controller;
        this.pheromoneMaxSmellingDistance = controller.scenarioMap.getPheromoneMaxSmellingDistance();
        this.pheromoneReduction = controller.scenarioMap.getPheromoneReduction();
    }

    public LinkedList<Tile> init(Vector2D[] startingAgentPositions, AgentType agentType) {
        LinkedList<Tile> tilesWithMarker = new LinkedList<>();
        int startIngIndex = agentType == AgentType.GUARD?0:controller.numberOfGuards;
        int endingIndex = agentType == AgentType.GUARD? controller.numberOfGuards:controller.numberOfGuards+controller.numberOfIntruders;
        for (int i = startIngIndex; i < endingIndex; i++) {
            Vector2D pos = startingAgentPositions[i];
            Agent agent = controller.getAgent(i);

            tilesWithMarker.add(controller.scenarioMap.getTile(pos));
            controller.scenarioMap.getTile(pos).addMarker(new PheromoneMarker(agent, pos, pheromoneMaxSmellingDistance, pheromoneReduction), agentType);
        }
        return tilesWithMarker;
    }

    protected void tick(){
        tick(AgentType.GUARD);
        tick(AgentType.INTRUDER);
    }

    protected void tick(AgentType agentType) {
        Iterator<Tile> iterator = controller.nextState.getTilesWithMarkerOf(agentType).iterator(); // *** This ALSO updates the marker in the currentState, while it's the same reference! ***
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            PheromoneMarker marker = tile.getPheromoneMarker(agentType);
            if (marker != null) {
                marker.updateMarker(controller.getTimestep());
                if (marker.shouldRemove()) {
                    tile.removeMarker(marker, agentType); // *** This ALSO removes the marker in the Tile of the currentState because same reference! ***
                    iterator.remove();
                }
            }
        }
        int startIngIndex = agentType == AgentType.GUARD?0:controller.numberOfGuards;
        int endingIndex = agentType == AgentType.GUARD? controller.numberOfGuards:controller.numberOfGuards+controller.numberOfIntruders;
        // Add the new pheromone markers
        for (int i = startIngIndex; i<endingIndex; i++) {
            addMarker(controller.nextState.getAgentPosition(i), new PheromoneMarker(controller.agents[i],
                    controller.nextState.getAgentPosition(i), pheromoneMaxSmellingDistance, pheromoneReduction), agentType);
        }
    }

    private void addMarker(Vector2D position, MarkerInterface marker, AgentType agentType) {
        if (position != null) {
            controller.scenarioMap.getTile(position).addMarker(marker, agentType);
            Iterator<Tile> iterator = controller.nextState.getTilesWithMarkerOf(agentType).iterator();
            while (iterator.hasNext()) {
                Tile tile = iterator.next();
                // Remove the old marker, in case the agent didn't move
                if (tile.getPheromoneMarker(agentType).getPosition().equals(position)) {
                    iterator.remove();
                    break;
                }
            }
            controller.nextState.addTileWithMarkerOf(controller.scenarioMap.getTile(position), agentType);
        }
    }

    private List<PheromoneMarker> getPheromoneMarkersCloseEnough(int agentIndex, AgentType agentType) {
        ArrayList<PheromoneMarker> markersCloseEnough = new ArrayList<>();
        for (Tile tile : controller.currentState.getTilesWithMarkerOf(agentType)) {
            if (( tile.getPheromoneMarker(agentType) != null && tile.getPheromoneMarker(agentType).getAgent() != controller.agents[agentIndex])
                    && controller.currentState.getAgentPosition(agentIndex).dist(tile.getPheromoneMarker(agentType).getPosition()) <= tile.getPheromoneMarker(agentType).getDistance()
                    && !controller.isWallInBetween(controller.currentState.getAgentPosition(agentIndex), tile.getPheromoneMarker(agentType).getPosition())) {
                markersCloseEnough.add(tile.getPheromoneMarker(agentType));
            }
        }
        return markersCloseEnough;
    }

    public double getPheromoneMarkersDirection(int agentIndex, Vector2D position, AgentType agentType) {
        List<PheromoneMarker> pheromoneMarkers = getPheromoneMarkersCloseEnough(agentIndex, agentType);
        if (pheromoneMarkers.size() == 0) return -1;

        double divider = 0;
        double angleSum = 0;
        for (PheromoneMarker pheromoneMarker : pheromoneMarkers) {
            divider += pheromoneMarker.getStrength();
            angleSum += position.getAngleBetweenVector(pheromoneMarker.getPosition()) * pheromoneMarker.getStrength();
        }
        return angleSum/divider;
    }
}
