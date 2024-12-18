package model.elements.grass.generators;

import java.util.Set;

import model.elements.grass.Grass;

public interface GrassGenerator {
    Set<Grass> generateGrass(int count);
}
