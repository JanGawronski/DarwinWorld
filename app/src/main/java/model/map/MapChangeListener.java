package model.map;

import model.Vector2d;

public interface MapChangeListener {
    void mapChanged(WorldMap map, Vector2d position);
}
