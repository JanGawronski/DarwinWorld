package model.elements.grass.generators;

import model.Vector2d;
import model.elements.grass.Grass;
import model.map.WorldMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractGrassGenerator implements GrassGenerator {
    protected final WorldMap map;
    protected final List<Vector2d> places;

    public AbstractGrassGenerator(WorldMap map) {
        this.map = map;
        this.places = new ArrayList<>();
        for (int x = 0; x < map.getWidth(); x++)
            for (int y = 0; y < map.getHeight(); y++) {
                places.add(new Vector2d(x, y));
            }
    }

    @Override
    public Set<Grass> generateGrass(int count) {
        if (count > map.getWidth() * map.getHeight() - map.getGrasses().size())
            throw new IllegalArgumentException("There is not enough space for " + count + " grasses");
        if (count < 0)
            throw new IllegalArgumentException("Count must be non-negative");

        List<Vector2d> preferredPositions = new ArrayList<>();
        List<Vector2d> notPreferredPositions = new ArrayList<>();
        Set<Vector2d> grassesPositions = map.getGrassesPositions();
        for (Vector2d position : places) {
            if (!grassesPositions.contains(position))
                if (isPreferred(position))
                    preferredPositions.add(position);
                else
                    notPreferredPositions.add(position);
        }

        HashSet<Grass> grasses = new HashSet<>();

        List<Vector2d> positions;
        for (int i = 0; i < count; i++) {
            positions = selectPositions(preferredPositions, notPreferredPositions);

            int index = ThreadLocalRandom.current().nextInt(positions.size());
            grasses.add(new Grass(positions.get(index)));
            positions.set(index, positions.getLast());
            positions.removeLast();
        }
        return grasses;
    }

    private List<Vector2d> selectPositions(List<Vector2d> preferred, List<Vector2d> notPreferred) {
        if (preferred.isEmpty())
            return notPreferred;
        if (notPreferred.isEmpty())
            return preferred;
        if (ThreadLocalRandom.current().nextInt(5) <= 3)
            return preferred;
        return notPreferred;
    }

    protected abstract boolean isPreferred(Vector2d position);

    @Override
    public List<Vector2d> getPreferred() {
        List<Vector2d> preferredPositions = new ArrayList<>();
        Set<Vector2d> grassesPositions = map.getGrassesPositions();
        for (Vector2d position : places)
            if (!grassesPositions.contains(position))
                if (isPreferred(position))
                    preferredPositions.add(position);

        return preferredPositions;
    }
}
