package model.elements.grass;

import model.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GrassTest {
    @Test
    void position() {
        for (int i = -10; i < 10; i++)
            for (int j = -10; j < 10; j++) {
                Grass grass = new Grass(new Vector2d(i, j));
                Vector2d position = grass.getPosition();

                assertEquals(position, grass.getPosition());
            }
    }

    @Test
    void isAt() {
        for (int i = -10; i < 10; i++)
            for (int j = -10; j < 10; j++) {
                Grass grass = new Grass(new Vector2d(i, j));
                Vector2d position = grass.getPosition();

                assertTrue(grass.isAt(position));
            }

        Vector2d position = new Vector2d(0, 0);
        Grass grass = new Grass(new Vector2d(1, 0));

        assertFalse(grass.isAt(position));

    }
}
