package model.elements;

import model.Vector2d;

public class Grass implements WorldElement {
    private final Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public boolean isAt(Vector2d otherPosition) {
        return position.equals(otherPosition);
    }

    @Override
    public String toString() {
        return "*";
    }
}
