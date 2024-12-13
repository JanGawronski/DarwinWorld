package model.map;

import model.MapDirection;
import model.Vector2d;
import model.elements.Animal;
import model.elements.WorldElement;

import java.util.*;

public class WorldMap {
    protected final Map<Vector2d, Animal> animals = new HashMap<>();
    private final MapVisualizer visualizer = new MapVisualizer(this);
    private final List<MapChangeListener> listeners = new ArrayList<>();
    private final Boundary boundary;

    public WorldMap(Boundary boundary) {
        this.boundary = boundary;
    }


    public void place(Animal animal) {
        animals.put(animal.getPosition(), animal);
        notifyMapChanged("place at " + animal.getPosition());
    }


    public boolean canMoveTo(Vector2d position) {
        return !animals.containsKey(position);
    }


    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position);
    }


    public WorldElement objectAt(Vector2d position) {
        return animals.get(position);
    }


    public void move(Animal animal) {
        if (!animals.containsValue(animal)) return;
        Vector2d prevPosition = animal.getPosition();
        MapDirection prevOrientation = animal.getOrientation();
        animals.remove(prevPosition);
        animals.put(animal.getPosition(), animal);
        if (animal.getPosition() != prevPosition || animal.getOrientation() != prevOrientation)
            notifyMapChanged("move at %s".formatted(prevPosition));
    }


    @Override
    public String toString() {
        return visualizer.draw(boundary.lowerLeft(), boundary.upperRight());
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
}