package gamelogic.controller;

import gamelogic.Vector2D;
import gamelogic.maps.MarkerInterface;
import gamelogic.maps.PheromoneMarker;
import gamelogic.maps.Tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MarkerController {

    public LinkedList<Tile> init(Vector2D[] agentPositions) {
        LinkedList<Tile> tilesWithMarker = new LinkedList<>();

    }
    protected void updateMarkers() {
        Iterator<Tile> iterator = tilesWithMarker.iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            MarkerInterface[] markers = tile.getMarkers();
            for (MarkerInterface marker : markers) {
                if (marker != null) {
                    marker.updateMarker(this.timestep);
                    if (marker.shouldRemove()) {
                        tile.removeMarker(marker);
                        iterator.remove();
                    }
                }
            }
        }
    }

    private List<PheromoneMarker> getPheromoneMarkersCloseEnough(int agentIndex) {
        ArrayList<PheromoneMarker> markersCloseEnough = new ArrayList<>();
        for (Tile tile : tilesWithMarker) {
            if (tile.getPheromoneMarker().getAgent() != agentsGuards[agentIndex]
                    && agentPositions[agentIndex].dist(tile.getPheromoneMarker().getPosition()) <= tile.getPheromoneMarker().getDistance()
                    && !isWallInBetween(agentPositions[agentIndex], tile.getPheromoneMarker().getPosition())) {
                markersCloseEnough.add(tile.getPheromoneMarker());
            }
        }
        return markersCloseEnough;
    }

    private double[] getPheromoneMarkersDirections(Vector2D agentPosition, List<PheromoneMarker> pheromoneMarkers) {
        double[] pheromoneMarkerDirections = new double[pheromoneMarkers.size()];
        for (int i = 0; i < pheromoneMarkers.size(); i++) {
            pheromoneMarkerDirections[i] = agentPosition.getAngleBetweenVector(pheromoneMarkers.get(i).getPosition());
        }
        return pheromoneMarkerDirections;
    }

    private boolean isWallInBetween(Vector2D begin, Vector2D end) {
        Vector2D[] positions = fov.calculateLine(begin, end);
        for (Vector2D pos : positions) {
            if (scMap.getTile(pos).isWall()) return true;
        }
        return false;
    }
}
