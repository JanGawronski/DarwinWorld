package model.map;

import model.MapDirection;
import model.Vector2d;
import model.util.Pair;

public interface MoveConverter {
    Pair<Vector2d, MapDirection> convert(Vector2d position, MapDirection orientation);
}
