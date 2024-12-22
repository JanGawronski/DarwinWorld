import org.junit.jupiter.api.Test;

import model.AnimalConfigData;
import model.map.WorldMap;
import model.elements.grass.generators.ForestedEquators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.lang.reflect.InvocationTargetException;

public class SimulationTest {
    @Test
    void initialSetup() {
        AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
        WorldMap map = new WorldMap(10, 10);
        new Simulation(map, config, new ForestedEquators(map), 10, 1, 10, 1);

        assertEquals(10, map.getGrasses().size());
        assertEquals(10, map.getAnimalsMap().values().stream().mapToInt(HashSet::size).sum());

        map.getAnimalsMap().values().stream()
                .flatMap(HashSet::stream)
                .forEach(animal -> assertEquals(1, animal.getEnergy()));
    }

    @Test
    void removeDeadAnimals() {
        try {
            Method removeDeadAnimals = Simulation.class.getDeclaredMethod("removeDeadAnimals");
            removeDeadAnimals.setAccessible(true);

            AnimalConfigData config = new AnimalConfigData(1, 1, 1, true, 1, 1, 1);
            WorldMap map = new WorldMap(10, 10);
            Simulation simulation1 = new Simulation(map, config, new ForestedEquators(map), 10, 1, 10, 0);

            removeDeadAnimals.invoke(simulation1);

            assertEquals(0, map.getAnimalsMap().values().stream().mapToInt(HashSet::size).sum());

            Simulation simulation2 = new Simulation(map, config, new ForestedEquators(map), 10, 1, 10, 1);
            
            removeDeadAnimals.invoke(simulation2);

            assertEquals(10, map.getAnimalsMap().values().stream().mapToInt(HashSet::size).sum());
        }

        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
