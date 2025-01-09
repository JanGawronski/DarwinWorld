package model.elements.grass;

import model.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
