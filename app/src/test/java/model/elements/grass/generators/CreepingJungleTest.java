package model.elements.grass.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import model.Vector2d;

public class CreepingJungleTest {
     @Test
    void IllegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> new CreepingJungle(-1, 10, Set.of(), 5));
        assertThrows(IllegalArgumentException.class, () -> new CreepingJungle(10, -1, Set.of(), 5));
        assertThrows(IllegalArgumentException.class, () -> new CreepingJungle(10, 10, Set.of(), -1));
    }

    @Test
    void count() {
        CreepingJungle creepingJungle = new CreepingJungle(10, 10, Set.of(), 5);
        int count = 0;
        for (Vector2d position : creepingJungle) {
            count++;
        }
        assertEquals(5, count);
    }

    @Test
    void countTooMuchToGenerate() {
        CreepingJungle creepingJungle = new CreepingJungle(5, 5, Set.of(), 100);
        int count = 0;
        for (Vector2d position : creepingJungle) {
            count++;
        }
        assertEquals(25, count);
    }

    @Test
    void countIfFilled() {
        Set<Vector2d> grassPositions = Set.of(new Vector2d(0, 0), new Vector2d(0, 1), new Vector2d(1, 0), new Vector2d(1, 1));

        CreepingJungle creepingJungle = new CreepingJungle(3, 3, grassPositions, 10);
        int count = 0;
        for (Vector2d position : creepingJungle) {
            count++;
        }
        assertEquals(5, count);
        
    }

    @Test
    void countIfFull() {
        Set<Vector2d> grassPositions = Set.of(new Vector2d(0, 0), new Vector2d(0, 1), new Vector2d(1, 0), new Vector2d(1, 1));

        CreepingJungle creepingJungle = new CreepingJungle(2, 2, grassPositions, 10);
        int count = 0;
        for (Vector2d position : creepingJungle) {
            count++;
        }
        assertEquals(0, count);   
    }

    @Test
    void unique() {
        CreepingJungle creepingJungle = new CreepingJungle(5, 5, Set.of(), 25);
        Vector2d[] positions = new Vector2d[25];
        int i = 0;
        for (Vector2d position : creepingJungle)
            positions[i++] = position;
        for (int j = 0; j < 25; j++)
            for (int k = j + 1; k < 25; k++)
                assertNotEquals(positions[j], positions[k]);
        
    }

    @Test
    void inBounds() {
        CreepingJungle creepingJungle = new CreepingJungle(5, 5, Set.of(), 25);
        for (Vector2d position : creepingJungle)
            assert(position.precedes(new Vector2d(4, 4)) && position.follows(new Vector2d(0, 0)));
        
    }
}
