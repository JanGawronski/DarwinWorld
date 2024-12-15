package model.map;

import model.MapDirection;
import model.Vector2d;
import model.elements.animal.Animal;
import model.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class WorldMap implements MoveConverter {
    private final List<MapChangeListener> listeners = new ArrayList<>();
    private final Vector2d lowerLeft;
    private final Vector2d upperRight;

    public WorldMap(int width, int height) {
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width, height);
    }


    public void place(Animal animal) {
        notifyMapChanged("place at " + animal.getPosition());
    }


    public void addListener(MapChangeListener listener) {
        listeners.add(listener);
    }


    public void removeListener(MapChangeListener listener) {
        listeners.remove(listener);
    }


    protected void notifyMapChanged(String message) {
        for (MapChangeListener listener : listeners) {
            listener.mapChanged(this, message);
        }
    }


    @Override
    public Pair<Vector2d, MapDirection> convert(Vector2d position, MapDirection orientation) {
        return null;
    }
}