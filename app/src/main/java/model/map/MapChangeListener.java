package model.map;

@FunctionalInterface
public interface MapChangeListener {
    void mapChanged(WorldMap worldMap, String message);
}
