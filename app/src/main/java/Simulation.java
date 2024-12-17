import model.AnimalConfigData;
import model.map.WorldMap;

public class Simulation {
    private final WorldMap map;
    private final AnimalConfigData animalConfig;

    public Simulation(WorldMap map, AnimalConfigData animalConfig) {
        this.map = map;
        this.animalConfig = animalConfig;
    }

    public void setup() {

    }
}
