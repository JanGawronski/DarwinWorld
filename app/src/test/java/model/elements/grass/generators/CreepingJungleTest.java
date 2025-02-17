package model.elements.grass.generators;

import model.Vector2d;
import model.elements.grass.Grass;
import model.map.WorldMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreepingJungleTest {
    @Test
    void doesntGenerateAlreadyGenerated() {
        WorldMap map = new WorldMap(2, 2);
        CreepingJungle creepingJungle = new CreepingJungle(map);
        map.place(new Grass(new Vector2d(0, 0)));
        map.place(new Grass(new Vector2d(0, 1)));
        map.place(new Grass(new Vector2d(1, 0)));
        Vector2d testPosition = new Vector2d(1, 1);

        for (int i = 0; i < 10; i++) {
            Grass grass = creepingJungle.generateGrass();
            map.place(grass);
            assertEquals(testPosition, grass.getPosition());
            map.remove(grass);
        }
    }

    @Test
    void inBounds() {
        WorldMap map = new WorldMap(10, 10);
        CreepingJungle creepingJungle = new CreepingJungle(map);

        for (int i = 0; i < 100; i++) {
            Vector2d position = creepingJungle.generateGrass().getPosition();
            assertTrue(map.inBounds(position));
        }
    }

    @Test
    void noFreePositions() {
        WorldMap map = new WorldMap(1, 1);
        CreepingJungle creepingJungle = new CreepingJungle(map);
        map.place(new Grass(new Vector2d(0, 0)));

        assertThrows(IllegalStateException.class, creepingJungle::generateGrass);
    }
}
