package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapDirectionTest {

    @Test
    void toMovementVector() {
        assertEquals(new Vector2d(0, 1), MapDirection.N.toMovementVector());
        assertEquals(new Vector2d(1, 1), MapDirection.NE.toMovementVector());
        assertEquals(new Vector2d(1, 0), MapDirection.E.toMovementVector());
        assertEquals(new Vector2d(1, -1), MapDirection.SE.toMovementVector());
        assertEquals(new Vector2d(0, -1), MapDirection.S.toMovementVector());
        assertEquals(new Vector2d(-1, -1), MapDirection.SW.toMovementVector());
        assertEquals(new Vector2d(-1, 0), MapDirection.W.toMovementVector());
        assertEquals(new Vector2d(-1, 1), MapDirection.NW.toMovementVector());
    }

    @Test
    void rotated() {
        assertEquals(MapDirection.N, MapDirection.N.rotated(0));
        assertEquals(MapDirection.SE, MapDirection.NW.rotated(4));
        assertEquals(MapDirection.W, MapDirection.NW.rotated(7));

        assertThrows(IllegalArgumentException.class, () -> MapDirection.N.rotated(-1));
    }
}