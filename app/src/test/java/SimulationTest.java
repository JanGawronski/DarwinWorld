import model.AnimalConfigData;
import model.Vector2d;
import model.map.WorldMap;
import org.junit.jupiter.api.Test;
import simulation.Simulation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {
    @Test
    void initialSetup() {
        AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
        WorldMap map = new WorldMap(10, 10);
        new Simulation(map, config, true, 10, 1, 10, 1);

        assertEquals(10, map.getGrasses().size());
        assertEquals(10, map.getAnimalPositions().stream().mapToInt(position -> map.getAnimalsAt(position).size()).sum());

        map.getAnimalPositions().stream().map(map::getAnimalsAt).forEach(animalSet -> animalSet.forEach(animal -> assertEquals(1, animal.getEnergy())));
    }

    @Test
    void removeDeadAnimals() {
        try {
            Method removeDeadAnimals = Simulation.class.getDeclaredMethod("removeDeadAnimals");
            removeDeadAnimals.setAccessible(true);

            AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
            WorldMap map = new WorldMap(10, 10);
            Simulation simulation1 = new Simulation(map, config, true, 10, 1, 10, 0);

            removeDeadAnimals.invoke(simulation1);

            assertEquals(0, map.getAnimalPositions().stream().mapToInt(position -> map.getAnimalsAt(position).size()).sum());

            Simulation simulation2 = new Simulation(map, config, true, 10, 1, 10, 1);

            removeDeadAnimals.invoke(simulation2);

            assertEquals(10, map.getAnimalPositions().stream().mapToInt(position -> map.getAnimalsAt(position).size()).sum());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    void eatGrass() {
        try {
            Method eatGrass = Simulation.class.getDeclaredMethod("eatGrass");
            eatGrass.setAccessible(true);

            AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
            WorldMap map1 = new WorldMap(10, 10);
            Simulation simulation1 = new Simulation(map1, config, true, 100, 1, 10, 1);

            eatGrass.invoke(simulation1);

            assertTrue(map1.getGrasses().size() < 100);
            assertTrue(map1.getGrasses().size() >= 90);

            assertTrue(map1.getAnimalPositions().stream().flatMap((Vector2d position) -> map1.getAnimalsAt(position).stream())
                    .anyMatch(animal -> animal.getEnergy() > 1));

            WorldMap map2 = new WorldMap(10, 10);
            Simulation simulation2 = new Simulation(map2, config, true, 0, 1, 10, 1);

            eatGrass.invoke(simulation2);

            assertEquals(0, map2.getGrasses().size());

            assertFalse(map2.getAnimalPositions().stream().flatMap((Vector2d position) -> map2.getAnimalsAt(position).stream())
                    .anyMatch(animal -> animal.getEnergy() > 1));

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    void breedAnimals() {
        try {
            Method breedAnimals = Simulation.class.getDeclaredMethod("breedAnimals");
            breedAnimals.setAccessible(true);

            AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
            WorldMap map1 = new WorldMap(10, 10);
            Simulation simulation1 = new Simulation(map1, config, true, 10, 1, 101, 1);

            breedAnimals.invoke(simulation1);

            assertTrue(map1.getAnimalPositions().stream().mapToInt(position -> map1.getAnimalsAt(position).size()).sum() > 101);
            assertTrue(map1.getAnimalPositions().stream().mapToInt(position -> map1.getAnimalsAt(position).size()).sum() <= 151);

            assertTrue(map1.getAnimalPositions().stream().flatMap((Vector2d position) -> map1.getAnimalsAt(position).stream())
                    .anyMatch(animal -> animal.getEnergy() > 1));

            assertTrue(map1.getAnimalPositions().stream().flatMap((Vector2d position) -> map1.getAnimalsAt(position).stream())
                    .anyMatch(animal -> animal.getEnergy() == 0));

            long countEnergyGreaterThanOne = map1.getAnimalPositions().stream()
                    .flatMap((Vector2d position) -> map1.getAnimalsAt(position).stream())
                    .filter(animal -> animal.getEnergy() > 1)
                    .count();
            long countEnergyEqualToZero = map1.getAnimalPositions().stream()
                    .flatMap((Vector2d position) -> map1.getAnimalsAt(position).stream())
                    .filter(animal -> animal.getEnergy() == 0)
                    .count();
            assertTrue(2 * countEnergyGreaterThanOne == countEnergyEqualToZero);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    void growGrass() {
        try {
            Method growGrass = Simulation.class.getDeclaredMethod("growGrass");
            growGrass.setAccessible(true);

            AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
            WorldMap map = new WorldMap(10, 10);
            Simulation simulation = new Simulation(map, config, true, 10, 50, 10, 1);

            growGrass.invoke(simulation);

            assertEquals(60, map.getGrasses().size());

            growGrass.invoke(simulation);

            assertEquals(100, map.getGrasses().size());

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}