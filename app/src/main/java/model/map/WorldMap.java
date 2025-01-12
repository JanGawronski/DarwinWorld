package model.map;

import model.MapDirection;
import model.Pair;
import model.Vector2d;
import model.elements.animal.Animal;
import model.elements.grass.Grass;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class WorldMap implements MoveConverter {
    private final HashMap<Vector2d, Set<Animal>> animals = new HashMap<>();
    private final HashMap<Vector2d, Grass> grasses = new HashMap<>();
    private final Vector2d lowerLeft;
    private final Vector2d upperRight;
    private int emptySquareCount;
    private final List<MapChangeListener> listeners = new ArrayList<>();

    public WorldMap(int height, int width) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Map dimensions must be positive");
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);
        this.emptySquareCount = width * height;
    }

    public void place(Animal animal) {
        Vector2d position = animal.getPosition();
        if (!animals.containsKey(position))
            animals.put(position, Collections.synchronizedSet(new HashSet<>()));
        if (animals.get(position).isEmpty() && !grasses.containsKey(position))
            emptySquareCount--;
        animals.get(position).add(animal);
    }

    public void remove(Animal animal) {
        Vector2d position = animal.getPosition();
        if (!animals.containsKey(position) || !animals.get(position).contains(animal))
            throw new IllegalArgumentException("This animal is not present at " + position);
        animals.get(position).remove(animal);
        if (animals.get(position).isEmpty() && !grasses.containsKey(position))
            emptySquareCount++;
    }

    public void place(Grass grass) {
        Vector2d position = grass.getPosition();
        if (grasses.containsKey(position))
            throw new IllegalArgumentException("Grass is already present at " + position);
        grasses.put(position, grass);
        if (!animals.containsKey(position) || animals.get(position).isEmpty())
            emptySquareCount--;
        mapChanged(position);
    }

    public void remove(Grass grass) {
        Vector2d position = grass.getPosition();
        if (!grasses.containsKey(position))
            throw new IllegalArgumentException("There is no grass at " + position);
        grasses.remove(position);
        if (!animals.containsKey(position) || animals.get(position).isEmpty())
            emptySquareCount++;
        mapChanged(position);
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
            return new Pair<>(new Vector2d(lowerLeft.x(), position.y()), orientation);
        return new Pair<>(position, orientation);
    }

    public Set<Vector2d> getAnimalPositions() {
        return animals.keySet().stream().filter(position -> !animals.get(position).isEmpty())
                .collect(Collectors.toSet());
    }

    public Set<Animal> getAnimalsAt(Vector2d position) {
        return Collections.unmodifiableSet(animals.getOrDefault(position, new HashSet<>()));
    }

    public boolean isAnimalAt(Vector2d position) {
        return animals.containsKey(position) && !animals.get(position).isEmpty();
    }

    public Animal getTopAnimalAt(Vector2d position) {
        synchronized (animals.get(position)) {
            if (!isAnimalAt(position))
                throw new IllegalArgumentException("There is no animal at " + position);
            return Collections.max(animals.get(position));
        }
    }

    public Set<Grass> getGrasses() {
        return Set.copyOf(grasses.values());
    }

    public Set<Vector2d> getGrassesPositions() {
        return Collections.unmodifiableSet(grasses.keySet());
    }

    public boolean isGrassAt(Vector2d position) {
        return grasses.containsKey(position);
    }

    public int getWidth() {
        return upperRight.x() + 1;
    }

    public int getHeight() {
        return upperRight.y() + 1;
    }

    public boolean inBounds(Vector2d position) {
        return position.follows(lowerLeft) && position.precedes(upperRight);
    }

    public int getEmptySquareCount() {
        return emptySquareCount;
    }

    public void addListener(MapChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MapChangeListener listener) {
        listeners.remove(listener);
    }

    public void mapChanged(Vector2d position) {
        listeners.forEach(listener -> listener.mapChanged(this, position));
    }

}