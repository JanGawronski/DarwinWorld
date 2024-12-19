package model.elements.grass.generators;

import model.elements.grass.Grass;

import java.util.Set;

public interface GrassGenerator {
    Set<Grass> generateGrass(int count);
}
