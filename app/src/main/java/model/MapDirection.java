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

    public static MapDirection randomDirection() {
        int chosenOrdinal = ThreadLocalRandom.current().nextInt(0, MapDirection.values().length);
        return MapDirection.values()[chosenOrdinal];
    }

}
