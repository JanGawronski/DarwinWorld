package model.elements.grass.generators;

import model.Vector2d;
import model.elements.grass.Grass;
import model.map.MapChangeListener;

import java.util.List;

public interface GrassGenerator  extends MapChangeListener{
    Grass generateGrass();

    List<Vector2d> getPreferred();
}
