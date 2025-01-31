package model.elements.grass.generators;

import model.MapDirection;
import model.Vector2d;
import model.map.WorldMap;

public class CreepingJungle extends AbstractGrassGenerator {
    public CreepingJungle(WorldMap map) {
        super(map);
    }

    @Override
    protected boolean isPreferred(Vector2d position) {
        for (MapDirection mapDirection : MapDirection.values()) {
            Vector2d neighbour = position.add(mapDirection.toMovementVector());
            if (map.inBounds(neighbour) && map.isGrassAt(neighbour))
                return true;
        }
        return false;
    }

    @Override
    public void mapChanged(WorldMap map, Vector2d position) {
        for (MapDirection mapDirection : MapDirection.values()) {
            Vector2d neighbour = position.add(mapDirection.toMovementVector());
            if (map.inBounds(neighbour) && !map.isGrassAt(neighbour))
                remove(neighbour);
        }

        super.mapChanged(map, position);

        for (MapDirection mapDirection : MapDirection.values()) {
            Vector2d neighbour = position.add(mapDirection.toMovementVector());
            if (map.inBounds(neighbour) && !map.isGrassAt(neighbour))
                add(neighbour);
        }
    }
}
