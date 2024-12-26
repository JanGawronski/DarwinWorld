package simulation;

import model.AnimalConfigData;
import model.map.WorldMap;

public record SimulationConfig(WorldMap map, AnimalConfigData animalConfigData, boolean defaultGrassGenerator,
        int initialGrassCount, int grassGrowthRate, int initialAnimalCount, int initialEnergy) {

    public SimulationConfig(int mapHeight, int mapWidth, int initialGrassCount, int feedEnergy,
            boolean defaultGrassGenerator, int grassGrowthRate, int initialAnimalCount, int initialEnergy,
            int saturationEnergy, int birthEnergy, int minMutations, int maxMutations, boolean defaultGeneSelector,
            int genomeLength) {

        this(
                new WorldMap(mapHeight, mapWidth),
                new AnimalConfigData(feedEnergy, birthEnergy, saturationEnergy, defaultGeneSelector, genomeLength,
                        minMutations, maxMutations),
                defaultGrassGenerator, initialGrassCount, grassGrowthRate, initialAnimalCount, initialEnergy);
    }
}
