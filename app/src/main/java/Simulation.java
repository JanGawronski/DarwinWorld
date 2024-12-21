import model.AnimalConfigData;
import model.Vector2d;
import model.elements.animal.Animal;
import model.elements.grass.Grass;
import model.elements.grass.generators.GrassGenerator;
import model.map.WorldMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation implements Runnable {
    private final HashSet<Animal> animals = new HashSet<>();
    private final HashSet<Animal> deadAnimals = new HashSet<>();
    private final WorldMap map;
    private final GrassGenerator grassGenerator;
    private final AnimalConfigData animalConfig;
    private final int grassGrowthRate;
    private int day = 0;

    public Simulation(WorldMap map, AnimalConfigData animalConfigData, GrassGenerator grassGenerator,
                      int initialGrassCount, int grassGrowthRate, int initialAnimalCount, int initialEnergy) {
        this.map = map;
        this.grassGenerator = grassGenerator;
        this.grassGrowthRate = grassGrowthRate;
        this.animalConfig = animalConfigData;

        if (initialGrassCount > map.getHeight() * map.getWidth())
            throw new IllegalArgumentException("Initial grass count cannot exceed map size");

        Set<Grass> grasses = grassGenerator.generateGrass(initialGrassCount);
        for (Grass grass : grasses)
            map.place(grass);

        for (int i = 0; i < initialAnimalCount; i++) {
            Vector2d position = new Vector2d(ThreadLocalRandom.current().nextInt(map.getWidth()),
                    ThreadLocalRandom.current().nextInt(map.getHeight()));
            Animal animal = new Animal(animalConfigData, initialEnergy, position);
            animals.add(animal);
            map.place(animal);
        }
    }

    @Override
    public void run() {
        removeDeadAnimals();
        moveAnimals();
        eatGrass();
        breedAnimals();
        growGrass();

        day++;

    }

    private void removeDeadAnimals() {
        for (Animal animal : animals)
            if (animal.getEnergy() == 0) {
                map.remove(animal);
                animals.remove(animal);
                deadAnimals.add(animal);
                animal.setDeathDay(day);
            }
    }

    private void moveAnimals() {
        for (Animal animal : animals) {
            map.remove(animal);
            animal.move(map);
            map.place(animal);
        }
    }

    private void eatGrass() {
        Set<Grass> grasses = map.getGrasses();
        for (Grass grass : grasses) {
            Set<Animal> animalsOnGrass = map.getAnimalsAt(grass.getPosition());
            if (animalsOnGrass.isEmpty())
                continue;
            Animal animal = Animal.sort(animalsOnGrass).getFirst();
            animal.eat();
            map.remove(grass);
        }
    }

    private void breedAnimals() {
        Map<Vector2d, HashSet<Animal>> animalsByPosition = map.getAnimalsMap();
        for (HashSet<Animal> positionAnimals : animalsByPosition.values()) {
            if (positionAnimals.size() >= 2) {
                List<Animal> sortedAnimals = Animal.sort(positionAnimals);
                if (sortedAnimals.get(1).getEnergy() >= animalConfig.saturationEnergy())
                    Animal.breed(sortedAnimals.get(0), sortedAnimals.get(1), this.animals.size() + this.deadAnimals.size());
            }
        }
    }

    private void growGrass() {
        Set<Grass> grasses = grassGenerator
                .generateGrass(Math.min(grassGrowthRate, map.getHeight() * map.getWidth() - map.getGrasses().size()));
        for (Grass grass : grasses)
            map.place(grass);
    }
}