package model.elements.grass.generators;

import model.MapDirection;
import model.Vector2d;
import model.map.WorldMap;

import java.util.Set;

public class CreepingJungle extends AbstractGrassGenerator {
    public CreepingJungle(WorldMap map) {
        super(map);
    }

    @Override
    protected boolean isPreferred(Vector2d position) {
        Set<Vector2d> grassPositions = map.getGrassesPositions();
        for (MapDirection mapDirection : MapDirection.values()) {
            Vector2d neighbour = position.add(mapDirection.toMovementVector());
            if (grassPositions.contains(neighbour))
                return true;
        }
        return false;
    }

}
