package gamelogic.controller;

import gamelogic.Vector2D;
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

    public MarkerController(Controller controller) {
        this.controller = controller;
    }

    public LinkedList<Tile> init(Vector2D[] startingAgentPositions) {
        LinkedList<Tile> tilesWithMarker = new LinkedList<>();
        for (int i = 0; i < controller.numberOfGuards+controller.numberOfIntruders; i++) {
            Vector2D pos = startingAgentPositions[i];
            Agent agent = controller.getAgent(i);

            tilesWithMarker.add(controller.scenarioMap.getTile(pos));
            controller.scenarioMap.getTile(pos).addMarker(new PheromoneMarker(agent, pos, controller.scenarioMap.getSmellingDistance()));
        }
        return tilesWithMarker;
    }
    protected void tick() {
        Iterator<Tile> iterator = controller.nextState.getTilesWithMarker().iterator(); // *** This ALSO updates the marker in the currentState, while it's the same reference! ***
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            MarkerInterface[] markers = tile.getMarkers();
            for (MarkerInterface marker : markers) {
                if (marker != null) {
                    marker.updateMarker(controller.getTimestep());
                    if (marker.shouldRemove()) {
                        tile.removeMarker(marker); // *** This ALSO removes the marker in the Tile of the currentState because same reference! ***
                        iterator.remove();
                    }
                }
            }
        }

        // Add the new pheromone markers
        for (int i = 0; i < controller.agents.length; i++) {
            addMarker(controller.nextState.getAgentPosition(i), new PheromoneMarker(controller.agents[i],
                    controller.nextState.getAgentPosition(i), controller.scenarioMap.getSmellingDistance()));
        }
    }

    private void addMarker(Vector2D position, MarkerInterface marker) {
        controller.scenarioMap.getTile(position).addMarker(marker);
        Iterator<Tile> iterator = controller.nextState.getTilesWithMarker().iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            // Remove the old marker, in case the agent didn't move
            if (tile.getPheromoneMarker().getPosition().equals(position)) {
                iterator.remove();
                break;
            }
        }
        controller.nextState.addTileWithMarker(controller.scenarioMap.getTile(position));
    }

    private List<PheromoneMarker> getPheromoneMarkersCloseEnough(int agentIndex) {
        ArrayList<PheromoneMarker> markersCloseEnough = new ArrayList<>();
        for (Tile tile : controller.currentState.getTilesWithMarker()) {
            if (tile.getPheromoneMarker().getAgent() != controller.agents[agentIndex]
                    && controller.currentState.getAgentPosition(agentIndex).dist(tile.getPheromoneMarker().getPosition()) <= tile.getPheromoneMarker().getDistance()
                    && !isWallInBetween(controller.currentState.getAgentPosition(agentIndex), tile.getPheromoneMarker().getPosition())) {
                markersCloseEnough.add(tile.getPheromoneMarker());
            }
        }
        return markersCloseEnough;
    }

    public double[] getPheromoneMarkersDirections(int agentIndex, Vector2D agentPosition) {
        List<PheromoneMarker> pheromoneMarkers = getPheromoneMarkersCloseEnough(agentIndex);
        double[] pheromoneMarkerDirections = new double[pheromoneMarkers.size()];
        for (int i = 0; i < pheromoneMarkers.size(); i++) {
            pheromoneMarkerDirections[i] = agentPosition.getAngleBetweenVector(pheromoneMarkers.get(i).getPosition());
        }
        return pheromoneMarkerDirections;
    }

    private boolean isWallInBetween(Vector2D begin, Vector2D end) {
        Vector2D[] positions = VisionController.calculateLine(begin, end);
        for (Vector2D pos : positions) {
            if (controller.scenarioMap.getTile(pos).isWall()) return true;
        }
        return false;
    }
}
