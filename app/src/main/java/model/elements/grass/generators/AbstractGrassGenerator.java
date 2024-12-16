package model.elements.grass.generators;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import model.Vector2d;

public abstract class AbstractGrassGenerator implements GrassGenerator {
    private final int count;
    private final List<Vector2d> preferredPositions;
    private final List<Vector2d> notPreferredPositions;

    public AbstractGrassGenerator(int width, int height, Collection<Vector2d> grassPositions, int count) {
        this.count = Math.min(count, width * height - grassPositions.size());

        preferredPositions = new java.util.ArrayList<>();
        notPreferredPositions = new java.util.ArrayList<>();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Vector2d position = new Vector2d(i, j);
                if (!grassPositions.contains(position))
                    if (isPreferred(position))
                        preferredPositions.add(position);
                    else
                        notPreferredPositions.add(position);
            }
    }

    protected abstract boolean isPreferred(Vector2d position);

    @Override
    public Iterator<Vector2d> iterator() {
        return new Iterator<Vector2d>() {
            private int generated = 0;

            @Override
            public boolean hasNext() {
                return generated < count && (!preferredPositions.isEmpty() || !notPreferredPositions.isEmpty());
            }

            @Override
            public Vector2d next() {
                if (!hasNext())
                    throw new java.util.NoSuchElementException("No more grass to generate");

                List<Vector2d> positions;
                if (ThreadLocalRandom.current().nextInt(5) <= 3 && !preferredPositions.isEmpty())
                    positions = preferredPositions;
                else
                    positions = notPreferredPositions;

                int index = ThreadLocalRandom.current().nextInt(positions.size());
                Vector2d position = positions.get(index);
                positions.set(index, positions.getLast());
                positions.removeLast();
                generated++;
                return position;
            }
        };
    }

}
