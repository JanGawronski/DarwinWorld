package model.map;

import model.MapDirection;
import model.Pair;
import model.Vector2d;
import model.elements.animal.Animal;
import model.elements.grass.Grass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class WorldMap implements MoveConverter {
    private final HashMap<Vector2d, HashSet<Animal>> animals = new HashMap<>();
    private final HashMap<Vector2d, Grass> grasses = new HashMap<>();
    private final List<MapChangeListener> listeners = new ArrayList<>();
    private final Vector2d lowerLeft;
    private final Vector2d upperRight;

    public WorldMap(int width, int height) {
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width, height);
    }

    public void place(Animal animal) {
        Vector2d position = animal.getPosition();
        if (!animals.containsKey(position))
            animals.put(position, new HashSet<Animal>());
        animals.get(position).add(animal);
        notifyMapChanged("Animal placed at " + animal.getPosition());
    }

    public void remove(Animal animal) {
        Vector2d position = animal.getPosition();
        if (!animals.containsKey(position) || !animals.get(position).contains(animal))
            throw new IllegalArgumentException("This animal is not present at " + position);
        animals.get(position).remove(animal);
        notifyMapChanged("Animal removed from " + animal.getPosition());
    }

    public void place(Grass grass) {
        Vector2d position = grass.getPosition();
        if (grasses.containsKey(position))
            throw new IllegalArgumentException("Grass is already present at " + position);
        grasses.put(position, grass);
        notifyMapChanged("Grass placed at " + grass.getPosition());
    }

    public void remove(Grass grass) {
        Vector2d position = grass.getPosition();
        if (!grasses.containsKey(position))
            throw new IllegalArgumentException("There is no grass at " + position);
        grasses.remove(position);
        notifyMapChanged("Grass removed from " + grass.getPosition());
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
        if (position.y() < lowerLeft.y() || position.y() > upperRight.y()) {
            MapDirection newOrientation = orientation.rotated(4);
            return new Pair<>(position.add(newOrientation.toMovementVector()), newOrientation);
        }
        if (position.x() < lowerLeft.x())
            return new Pair<>(new Vector2d(upperRight.x(), position.y()), orientation);
        if (position.x() > upperRight.x())
            return new Pair<>(new Vector2d(lowerLeft.x(), position.x()), orientation);
        return new Pair<>(position, orientation);
    }
 
    public Map<Vector2d, HashSet<Animal>> getAnimalsMap() {
        return Collections.unmodifiableMap(animals);
    }

    public Set<Animal> getAnimalsAt(Vector2d position) {
        return Collections.unmodifiableSet(animals.getOrDefault(position, new HashSet<Animal>()));
    }

    public Set<Grass> getGrasses() {
        return Collections.unmodifiableSet(new HashSet<Grass>(grasses.values()));
    }

    public Set<Vector2d> getGrassesPositions() {
        return Collections.unmodifiableSet(grasses.keySet());
    }

    public int getWidth() {
        return upperRight.x();
    }

    public int getHeight() {
        return upperRight.y();
    }

}