package model.elements;

import model.MapDirection;
import model.Vector2d;

public class Animal implements WorldElement {
    private MapDirection orientation;
    private Vector2d position;

    public Animal(MapDirection orientation, Vector2d position) {
        this.orientation = orientation;
        this.position = position;
    }

    public Animal(MapDirection orientation) {
        this(orientation, new Vector2d(2, 2));
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public boolean isAt(Vector2d otherPosition) {
        return this.position.equals(otherPosition);
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public void move() {

    }

    @Override
    public String toString() {
        return this.orientation.toString();
    }
}
