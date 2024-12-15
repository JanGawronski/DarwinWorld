package model;

import java.util.concurrent.ThreadLocalRandom;

public enum MapDirection {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    private final static Vector2d[] movementVectors = new Vector2d[]{
            new Vector2d(0, 1),
            new Vector2d(1, 1),
            new Vector2d(1, 0),
            new Vector2d(1, -1),
            new Vector2d(0, -1),
            new Vector2d(-1, -1),
            new Vector2d(-1, 0),
            new Vector2d(-1, 1)
    };

    public static MapDirection randomDirection() {
        int chosenOrdinal = ThreadLocalRandom.current().nextInt(0, MapDirection.values().length);
        return MapDirection.values()[chosenOrdinal];
    }

    public Vector2d toMovementVector() {
        return MapDirection.movementVectors[this.ordinal()];
    }

    public MapDirection rotated(int rotationSteps) {
        if (rotationSteps < 0)
            throw new IllegalArgumentException("Rotation cannot be negative");
        int newOrdinal = (this.ordinal() + rotationSteps) % 8;
        return MapDirection.values()[newOrdinal];
    }
}
