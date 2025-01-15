package model.elements.grass.generators;

import model.Vector2d;
import model.map.WorldMap;

public class ForestedEquators extends AbstractGrassGenerator {
    public ForestedEquators(WorldMap map) {
        super(map);
    }

    @Override
    protected boolean isPreferred(Vector2d position) {
        return position.y() >= 4 * map.getHeight() / 10 && position.y() < 6 * map.getHeight() / 10;
    }

}
