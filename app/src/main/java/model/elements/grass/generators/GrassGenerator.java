package model.elements.grass.generators;

import model.Vector2d;
import model.elements.grass.Grass;

import java.util.List;
import java.util.Set;

public interface GrassGenerator {
    Set<Grass> generateGrass(int count);

    List<Vector2d> getPreferred();
}
