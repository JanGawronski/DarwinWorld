package model.elements.grass.generators;

import model.Vector2d;
import model.elements.grass.Grass;
import model.map.WorldMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractGrassGenerator implements GrassGenerator {
    protected final WorldMap map;
    private final List<Vector2d> preferred = new ArrayList<>();
    private final List<Vector2d> notPreferred = new ArrayList<>();
    private final HashMap<Vector2d, Integer> preferredIndices = new HashMap<>();
    private final HashMap<Vector2d, Integer> notPreferredIndices = new HashMap<>();

    public AbstractGrassGenerator(WorldMap map) {
        this.map = map;
        map.addListener(this);
        for (int x = 0; x < map.getWidth(); x++)
            for (int y = 0; y < map.getHeight(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (isPreferred(position)) {
                    preferred.add(position);
                    preferredIndices.put(position, preferred.size() - 1);
                } else {
                    notPreferred.add(position);
                    notPreferredIndices.put(position, notPreferred.size() - 1);
                }
            }
    }

    @Override
    public Grass generateGrass() {
        List<Vector2d> positions = selectPositions(preferred, notPreferred);
        int index = ThreadLocalRandom.current().nextInt(positions.size());
        return new Grass(positions.get(index));
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

    @Override
    public void mapChanged(WorldMap map, Vector2d position) {
        if (map.isGrassAt(position))
            remove(position);
        else
            add(position);
    }

    protected void add(Vector2d position) {
        List<Vector2d> positions;
        HashMap<Vector2d, Integer> indices;
        if (isPreferred(position)) {
            positions = preferred;
            indices = preferredIndices;
        } else {
            positions = notPreferred;
            indices = notPreferredIndices;
        }
        
        positions.add(position);
        indices.put(position, positions.size() - 1);
    }

    protected void remove(Vector2d position) {
        List<Vector2d> positions;
        HashMap<Vector2d, Integer> indices;
        if (preferredIndices.containsKey(position)) {
            positions = preferred;
            indices = preferredIndices;
        } else {
            positions = notPreferred;
            indices = notPreferredIndices;
        }

        int index = indices.get(position);
        Vector2d last = positions.getLast();
        positions.set(index, last);
        indices.put(last, index);
        positions.removeLast();
        indices.remove(position);
    }

    @Override
    public List<Vector2d> getPreferred() {
        return new ArrayList<>(preferred);
    }

    protected abstract boolean isPreferred(Vector2d position);
}
