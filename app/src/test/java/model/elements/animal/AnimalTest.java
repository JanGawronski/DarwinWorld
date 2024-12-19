package model.elements.animal;

import model.AnimalConfigData;
import model.Vector2d;
import model.map.WorldMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnimalTest {

    @Test
    public void sort() {
        AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
        Vector2d position = new Vector2d(0, 0);
        Animal animal1 = new Animal(config, 20, position);
        Animal animal2 = new Animal(config, 11, position);
        Animal animal3 = new Animal(config, 10, position);
        Animal animal4 = new Animal(config, 9, position);
        Animal animal5 = Animal.breed(animal2, animal3, 0);

        WorldMap map = new WorldMap(10, 10);
        animal2.move(map);


        assertEquals(List.of(animal1, animal2, animal3, animal4, animal5), Animal.sort(Set.of(animal1, animal2, animal3, animal4, animal5)));
    }

}
