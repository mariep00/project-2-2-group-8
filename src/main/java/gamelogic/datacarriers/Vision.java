package gamelogic.datacarriers;

import datastructures.Vector2D;
import gamelogic.maps.Tile;

public record Vision(Tile tile, Vector2D position) {}
