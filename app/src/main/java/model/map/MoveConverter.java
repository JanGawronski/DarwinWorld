package model.map;

import model.MapDirection;
import model.Pair;
import model.Vector2d;

@FunctionalInterface
public interface MoveConverter {
    Pair<Vector2d, MapDirection> convert(Vector2d position, MapDirection orientation);
}
