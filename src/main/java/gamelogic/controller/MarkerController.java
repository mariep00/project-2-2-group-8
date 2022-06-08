package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.maps.MarkerInterface;
import gamelogic.maps.PheromoneMarker;
import gamelogic.maps.Tile;

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

    public LinkedList<Tile> init(Vector2D[] startingAgentPositions) {
        LinkedList<Tile> tilesWithMarker = new LinkedList<>();
        for (int i = 0; i < controller.numberOfGuards+controller.numberOfIntruders; i++) {
            Vector2D pos = startingAgentPositions[i];
            Agent agent = controller.getAgent(i);

            tilesWithMarker.add(controller.scenarioMap.getTile(pos));
            controller.scenarioMap.getTile(pos).addMarker(new PheromoneMarker(agent, pos, pheromoneMaxSmellingDistance, pheromoneReduction));
        }
        return tilesWithMarker;
    }
    protected void tick() {
        Iterator<Tile> guardIterator = controller.nextState.getTilesWithMarkerGuard().iterator(); // *** This ALSO updates the marker in the currentState, while it's the same reference! ***
        Iterator<Tile> intruderIterator = controller.nextState.getTilesWithMarkerIntruder().iterator();
        while (guardIterator.hasNext() || intruderIterator.hasNext()) {
            Tile guardTile = guardIterator.next();
            Tile intruderTile = intruderIterator.next();
            MarkerInterface[] guardMarkers = guardTile.getMarkers();
            MarkerInterface[] intruderMarkers = intruderTile.getMarkers();
            for (MarkerInterface guardMarker : guardMarkers) {
                if (guardMarker != null) {
                    guardMarker.updateMarker(controller.getTimestep());
                    if (guardMarker.shouldRemove()) {
                        guardTile.removeMarker(guardMarker); // *** This ALSO removes the marker in the Tile of the currentState because same reference! ***
                        guardIterator.remove();
                    }
                }
            }

            for (MarkerInterface intruderMarker : intruderMarkers) {
                if (intruderMarker != null) {
                    intruderMarker.updateMarker(controller.getTimestep());
                    if (intruderMarker.shouldRemove()) {
                        intruderTile.removeMarker(intruderMarker); // *** This ALSO removes the marker in the Tile of the currentState because same reference! ***
                        intruderIterator.remove();
                    }
                }
            }
        }

        // Add the new pheromone markers for the guards
        for (int i = 0; i < controller.numberOfGuards; i++) {
            addGuardMarker(controller.nextState.getAgentPosition(i), new PheromoneMarker(controller.agents[i],
                    controller.nextState.getAgentPosition(i), pheromoneMaxSmellingDistance, pheromoneReduction));
        }

        // Add the new pheromone markers for the intruders
        for (int i = 0; i < controller.numberOfIntruders; i++) {
            addIntruderMarker(controller.nextState.getAgentPosition(i), new PheromoneMarker(controller.agents[i],
                    controller.nextState.getAgentPosition(i), pheromoneMaxSmellingDistance, pheromoneReduction));
        }

    }

    private void addGuardMarker(Vector2D position, MarkerInterface marker) {
        controller.scenarioMap.getTile(position).addMarker(marker);
        Iterator<Tile> iterator = controller.nextState.getTilesWithMarkerGuard().iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            // Remove the old marker, in case the agent didn't move
            if (tile.getPheromoneMarker().getPosition().equals(position)) {
                iterator.remove();
                break;
            }
        }
        controller.nextState.addTileWithMarkerGuard(controller.scenarioMap.getTile(position));
    }

    private void addIntruderMarker(Vector2D position, MarkerInterface marker) {
        controller.scenarioMap.getTile(position).addMarker(marker);
        Iterator<Tile> iterator = controller.nextState.getTilesWithMarkerIntruder().iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            // Remove the old marker, in case the agent didn't move
            if (tile.getPheromoneMarker().getPosition().equals(position)) {
                iterator.remove();
                break;
            }
        }
        controller.nextState.addTileWithMarkerIntruder(controller.scenarioMap.getTile(position));
    }

    private List<PheromoneMarker> getGuardsPheromoneMarkersCloseEnough(int agentIndex) {
        ArrayList<PheromoneMarker> markersCloseEnough = new ArrayList<>();
        for (Tile tile : controller.currentState.getTilesWithMarkerGuard()) {
            if (tile.getPheromoneMarker().getAgent() != controller.agents[agentIndex]
                    && controller.currentState.getAgentPosition(agentIndex).dist(tile.getPheromoneMarker().getPosition()) <= tile.getPheromoneMarker().getDistance()
                    && !controller.isWallInBetween(controller.currentState.getAgentPosition(agentIndex), tile.getPheromoneMarker().getPosition())) {
                markersCloseEnough.add(tile.getPheromoneMarker());
            }
        }
        return markersCloseEnough;
    }

    private List<PheromoneMarker> getIntrudersPheromoneMarkersCloseEnough(int agentIndex) {
        ArrayList<PheromoneMarker> markersCloseEnough = new ArrayList<>();
        for (Tile tile : controller.currentState.getTilesWithMarkerIntruder()) {
            if (tile.getPheromoneMarker().getAgent() != controller.agents[agentIndex]
                    && controller.currentState.getAgentPosition(agentIndex).dist(tile.getPheromoneMarker().getPosition()) <= tile.getPheromoneMarker().getDistance()
                    && !controller.isWallInBetween(controller.currentState.getAgentPosition(agentIndex), tile.getPheromoneMarker().getPosition())) {
                markersCloseEnough.add(tile.getPheromoneMarker());
            }
        }
        return markersCloseEnough;
    }

    public double getGuardsPheromoneMarkersDirection(int agentIndex, Vector2D agentPosition) {
        List<PheromoneMarker> pheromoneMarkers = getGuardsPheromoneMarkersCloseEnough(agentIndex);
        if (pheromoneMarkers.size() == 0) return -1;

        double divider = 0;
        double angleSum = 0;
        for (PheromoneMarker pheromoneMarker : pheromoneMarkers) {
            divider += pheromoneMarker.getStrength();
            angleSum += agentPosition.getAngleBetweenVector(pheromoneMarker.getPosition()) * pheromoneMarker.getStrength();
        }
        return angleSum/divider;
    }

    public double getIntrudersPheromoneMarkersDirection(int agentIndex, Vector2D agentPosition) {
        List<PheromoneMarker> pheromoneMarkers = getIntrudersPheromoneMarkersCloseEnough(agentIndex);
        if (pheromoneMarkers.size() == 0) return -1;

        double divider = 0;
        double angleSum = 0;
        for (PheromoneMarker pheromoneMarker : pheromoneMarkers) {
            divider += pheromoneMarker.getStrength();
            angleSum += agentPosition.getAngleBetweenVector(pheromoneMarker.getPosition()) * pheromoneMarker.getStrength();
        }
        return angleSum/divider;
    }
}
