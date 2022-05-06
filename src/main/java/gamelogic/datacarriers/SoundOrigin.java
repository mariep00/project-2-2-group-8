package gamelogic.datacarriers;

import datastructures.Vector2D;

public record SoundOrigin(Vector2D origin, SoundType soundType, int agentIndex) {}

