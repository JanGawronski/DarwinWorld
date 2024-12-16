package model.elements.grass.generators;

import java.util.Collection;

import model.Vector2d;
import model.MapDirection;

public class CreepingJungle extends AbstractGrassGenerator {
    private final Collection<Vector2d> grassPositions;

    public CreepingJungle(int width, int height, Collection<Vector2d> grassPositions, int count) {
        super(width, height, grassPositions, count);
        this.grassPositions = grassPositions;
    }

    @Override
    protected boolean isPreferred(Vector2d position) {
        if (grassPositions == null || grassPositions.isEmpty())
            return false;
        for (MapDirection mapDirection : MapDirection.values()) {
            Vector2d neighbour = position.add(mapDirection.toMovementVector());
            if (grassPositions.contains(neighbour))
                return true;
        }
        return false;
    }

}
