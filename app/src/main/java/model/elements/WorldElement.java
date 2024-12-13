package model.elements;

import model.Vector2d;

public interface WorldElement {
    Vector2d getPosition();

    boolean isAt(Vector2d otherPosition);
}
