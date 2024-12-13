package model;

public enum MapDirection {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    private final static Vector2d[] unitVectors =
            {new Vector2d(0, 1), new Vector2d(1, 0), new Vector2d(0, -1), new Vector2d(-1, 0)};


    public MapDirection next() {
        return MapDirection.values()[(this.ordinal() + 1) % 4];
    }

    public MapDirection previous() {
        return MapDirection.values()[(this.ordinal() + 3) % 4];
    }

    public Vector2d toUnitVector() {
        return unitVectors[this.ordinal()];
    }
}
