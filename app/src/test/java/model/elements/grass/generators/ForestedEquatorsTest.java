package model.elements.grass.generators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import model.Vector2d;
import model.elements.grass.Grass;
import model.map.WorldMap;

public class ForestedEquatorsTest {
    @Test
    void illegalArguments() {
        WorldMap map = new WorldMap(10, 10);
        ForestedEquators forestedEquators = new ForestedEquators(map);

        assertThrows(IllegalArgumentException.class, () -> forestedEquators.generateGrass(-1));
        assertThrows(IllegalArgumentException.class, () -> forestedEquators.generateGrass(1000));
    }

    @Test
    void count() {
        WorldMap map = new WorldMap(10, 10);
        ForestedEquators forestedEquators = new ForestedEquators(map);
        int count = 5;
        Set<Grass> grasses = forestedEquators.generateGrass(count);
        assertEquals(count, grasses.size());
    }

    @Test
    void ifDoesntGenerateAlreadyGenerated() {
        WorldMap map = new WorldMap(2, 2);
        map.place(new Grass(new Vector2d(0, 0)));
        map.place(new Grass(new Vector2d(0, 1)));
        map.place(new Grass(new Vector2d(1, 0)));
        ForestedEquators forestedEquators = new ForestedEquators(map);
        Vector2d testPosition = new Vector2d(1, 1);

        for (int i = 0; i < 10; i++) {
            Set<Grass> grasses = forestedEquators.generateGrass(1);
            assertEquals(testPosition, grasses.iterator().next().getPosition());
        }
    }


    @Test
    void unique() {
        WorldMap map = new WorldMap(10, 10);
        ForestedEquators forestedEquators = new ForestedEquators(map);
        Grass grasses[] = forestedEquators.generateGrass(100).toArray(Grass[]::new);
        for (int i = 0; i < 100; i++)
            for (int j = i + 1; j < 100; j++)
                assertNotEquals(grasses[i].getPosition(), grasses[j].getPosition());
    }

    @Test
    void inBounds() {
        WorldMap map = new WorldMap(10, 10);
        ForestedEquators forestedEquators = new ForestedEquators(map);
        Set<Grass> grasses = forestedEquators.generateGrass(100);
        Vector2d lowerLeft = new Vector2d(0, 0);
        Vector2d upperRight = new Vector2d(9, 9);
        for (Grass grass : grasses) {
            assertTrue(grass.getPosition().precedes(upperRight) && grass.getPosition().follows(lowerLeft));
        }
        
    }
}
