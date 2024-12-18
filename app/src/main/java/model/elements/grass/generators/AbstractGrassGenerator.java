package model.elements.grass.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Set;

import model.Vector2d;
import model.elements.grass.Grass;
import model.map.WorldMap;

public abstract class AbstractGrassGenerator implements GrassGenerator {
    protected final WorldMap map;

    public AbstractGrassGenerator(WorldMap map) {
        this.map = map;
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
        for (int x = 0; x < map.getWidth(); x++)
            for (int y = 0; y < map.getHeight(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (!grassesPositions.contains(position))
                    if (isPreferred(position))
                        preferredPositions.add(position);
                    else
                        notPreferredPositions.add(position);
            }

        HashSet<Grass> grasses = new HashSet<>();

        List<Vector2d> positions;
        for (int i = 0; i < count; i++) {
            if (ThreadLocalRandom.current().nextInt(5) <= 3 && !preferredPositions.isEmpty())
                positions = preferredPositions;
            else
                positions = notPreferredPositions;
            
            int index = ThreadLocalRandom.current().nextInt(positions.size());
            grasses.add(new Grass(positions.get(index)));
            positions.set(index, positions.getLast());
            positions.removeLast();
        }
        return grasses;
    }

    protected abstract boolean isPreferred(Vector2d position);

}
