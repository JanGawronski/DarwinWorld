package model.map;

import model.Vector2d;

@FunctionalInterface
public interface MapChangeListener {
    void mapChanged(WorldMap worldMap, Vector2d position);
}
