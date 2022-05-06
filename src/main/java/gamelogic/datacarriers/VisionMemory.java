package gamelogic.datacarriers;
import datastructures.Vector2D;

public record VisionMemory(Vector2D position, double secondsAgo, double orientation) { }
