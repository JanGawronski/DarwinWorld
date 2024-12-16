package model.elements.grass.generators;

import java.util.Collection;

import model.Vector2d;

public class ForestedEquators extends AbstractGrassGenerator {
    private final int height;

    public ForestedEquators(int width, int height, Collection<Vector2d> grassPositions, int count) {
        super(width, height, grassPositions, count);
        this.height = height;
    }

    @Override
    protected boolean isPreferred(Vector2d position) {
        return position.y() > 4 * height / 10 && position.y() < 6 * height / 10;
    }

}
