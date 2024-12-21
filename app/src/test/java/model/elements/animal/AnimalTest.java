package model.elements.animal;

import model.AnimalConfigData;
import model.MapDirection;
import model.Pair;
import model.Vector2d;
import model.map.MoveConverter;
import model.map.WorldMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalTest {

    @Test
    public void sorted() {
        AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
        Vector2d position = new Vector2d(0, 0);
        Animal animal1 = new Animal(config, 20, position);
        Animal animal2 = new Animal(config, 11, position);
        Animal animal3 = new Animal(config, 10, position);
        Animal animal4 = new Animal(config, 9, position);
        Animal animal5 = null;
        try {
            animal5 = Animal.breed(animal2, animal3, 0);
        } catch (ParentNotSaturatedException e) {
            fail();
        }


        WorldMap map = new WorldMap(10, 10);
        animal2.move(map);


        assertEquals(List.of(animal1, animal2, animal3, animal4, animal5), Animal.sorted(Set.of(animal1, animal2, animal3, animal4, animal5)));
    }

    @Test
    void breed() {
        AnimalConfigData config = new AnimalConfigData(0, 1, 1, true, 1, 0, 0);
        AnimalConfigData config2 = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
        Vector2d p = new Vector2d(0, 0);
        Animal a0 = new Animal(config, 100, p);
        Animal a1 = new Animal(config, 80, p);
        Animal a2 = new Animal(config, 10, p);
        Animal different1 = new Animal(config2, 100, p);
        Animal different2 = new Animal(config, 100, new Vector2d(1, 1));

        assertThrows(IllegalArgumentException.class, () -> Animal.breed(a1, different1, 0));
        assertThrows(IllegalArgumentException.class, () -> Animal.breed(a2, different2, 0));
        assertThrows(IllegalArgumentException.class, () -> Animal.breed(a1, a1, 0));

        try {
            Animal c3 = Animal.breed(a0, a1, 1);
            Animal c4 = Animal.breed(a0, a1, 2);
            Animal c5 = Animal.breed(a0, c3, 3);
            Animal c6 = Animal.breed(a0, a2, 4);
            Animal c7 = Animal.breed(c3, c4, 5);


            AnimalStats[] st = new AnimalStats[]{a0.getStats(), a1.getStats(), a2.getStats(), c3.getStats(), c4.getStats(), c5.getStats(), c6.getStats(), c7.getStats()};

            assertEquals(96, st[0].energy());
            assertEquals(78, st[1].energy());
            assertEquals(9, st[2].energy());
            assertEquals(0, st[3].energy());
            assertEquals(1, st[4].energy());
            assertEquals(2, st[5].energy());
            assertEquals(2, st[6].energy());

            assertEquals(5, st[0].descendants());
            assertEquals(4, st[1].descendants());
            assertEquals(1, st[2].descendants());
            assertEquals(2, st[3].descendants());
            assertEquals(1, st[4].descendants());
            for (int i = 5; i <= 7; i++)
                assertEquals(0, st[i].descendants());

            assertEquals(4, st[0].children());
            assertEquals(2, st[1].children());
            assertEquals(1, st[2].children());
            assertEquals(2, st[3].children());
            assertEquals(1, st[4].children());
            for (int i = 5; i <= 7; i++)
                assertEquals(0, st[i].children());

            assertThrows(ParentNotSaturatedException.class, () -> Animal.breed(c3, a0, 10));
        } catch (ParentNotSaturatedException e) {
            fail();
        }

    }

    @Test
    void eat() {
        AnimalConfigData c1 = new AnimalConfigData(5, 1, 1, true, 1, 1, 1);
        AnimalConfigData c2 = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
        Vector2d p = new Vector2d(0, 0);
        Animal a1 = new Animal(c1, 100, p);
        Animal a2 = new Animal(c2, 0, p);

        for (int i=0;i<20;i++)
            a1.eat();
        a2.eat();

        assertEquals(200, a1.getEnergy());
        assertEquals(1, a2.getEnergy());
        assertEquals(20, a1.getStats().grassEaten());
        assertEquals(1, a2.getStats().grassEaten());
    }

    @Test
    void move() {
        MoveConverter dummyConverter = Pair::new;
        Genome g = new Genome(new int[]{0, 2, 1, 6});
        AnimalConfigData c = new AnimalConfigData(5, 1, 1, true, g.length(), 1, 1);
        Animal animal = new Animal(c, 10, g, 0, MapDirection.N, new Vector2d(0, 0), null);

        animal.move(dummyConverter);
        assertEquals(MapDirection.N, animal.getOrientation());
        assertEquals(new Vector2d(0, 1), animal.getPosition());
        animal.move(dummyConverter);
        assertEquals(MapDirection.E, animal.getOrientation());
        assertEquals(new Vector2d(1, 1), animal.getPosition());
        animal.move(dummyConverter);
        assertEquals(MapDirection.SE, animal.getOrientation());
        assertEquals(new Vector2d(2, 0), animal.getPosition());
        animal.move(dummyConverter);
        assertEquals(MapDirection.NE, animal.getOrientation());
        assertEquals(new Vector2d(3, 1), animal.getPosition());
        animal.move(dummyConverter);
        assertEquals(MapDirection.NE, animal.getOrientation());
        assertEquals(new Vector2d(4, 2), animal.getPosition());
    }
}
