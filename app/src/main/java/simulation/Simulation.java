package simulation;
import model.AnimalConfigData;
import model.Vector2d;
import model.elements.animal.Animal;
import model.elements.animal.Genome;
import model.elements.animal.ParentNotSaturatedException;
import model.elements.grass.Grass;
import model.elements.grass.generators.GrassGenerator;
import model.map.WorldMap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Simulation implements Runnable {
    private final Set<Animal> animals = Collections.newSetFromMap(new ConcurrentHashMap<Animal, Boolean>());
    private final Set<Animal> deadAnimals = Collections.newSetFromMap(new ConcurrentHashMap<Animal, Boolean>());
    private final WorldMap map;
    private final GrassGenerator grassGenerator;
    private final int grassGrowthRate;
    private final ConcurrentHashMap<Genome, Integer> genomePopularity = new ConcurrentHashMap<>();
    private int day = 0;
    private ExecutorService executor;
    private volatile int speed = 1;

    public Simulation(WorldMap map, AnimalConfigData animalConfigData, GrassGenerator grassGenerator,
                      int initialGrassCount, int grassGrowthRate, int initialAnimalCount, int initialEnergy) {
        this.map = map;
        this.grassGenerator = grassGenerator;
        this.grassGrowthRate = grassGrowthRate;

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
            genomePopularity.merge(animal.getGenome(), 1, Integer::sum);
            this.map.place(animal);
        }
    }

    @Override
    public void run() {
        while (true) {
            removeDeadAnimals();
            moveAnimals();
            eatGrass();
            breedAnimals();
            growGrass();

            day++;
            map.notifyMapChanged();

            try {
                Thread.sleep(1000 / speed);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void start() {
        if (!(executor == null || executor.isTerminated() || executor.isShutdown()))
            throw new IllegalStateException("Simulation is not stopped");
        executor = Executors.newSingleThreadExecutor();
        executor.submit(this);
    }

    public void stop() {
        try {
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    private void removeDeadAnimals() {
        for (Animal animal : animals)
            if (animal.getEnergy() == 0) {
                map.remove(animal);
                genomePopularity.computeIfPresent(animal.getGenome(), (k, v) -> (v - 1 == 0) ? null : v - 1);
                deadAnimals.add(animal);
                animal.setDeathDay(day);
            }
        animals.removeIf(animal -> animal.getEnergy() == 0);
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
            Animal animal = Collections.max(animalsOnGrass);
            animal.eat();
            map.remove(grass);
        }
    }

    private void breedAnimals() {
        Map<Vector2d, HashSet<Animal>> animalsByPosition = map.getAnimalsMap();
        for (HashSet<Animal> positionAnimals : animalsByPosition.values()) {
            if (positionAnimals.size() >= 2) {
                Animal first = Collections.max(positionAnimals);
                Animal second = Collections.max(positionAnimals.stream().filter(animal -> animal != first).toList());
                try {
                    Animal child = Animal.breed(first, second, this.animals.size() + this.deadAnimals.size());
                    animals.add(child);
                    genomePopularity.merge(child.getGenome(), 1, Integer::sum);
                    map.place(child);
                } catch (ParentNotSaturatedException e) {
                    // not enough energy in parents, no child
                }

            }
        }
    }

    private void growGrass() {
        Set<Grass> grasses = grassGenerator
                .generateGrass(Math.min(grassGrowthRate, map.getHeight() * map.getWidth() - map.getGrasses().size()));
        for (Grass grass : grasses)
            map.place(grass);
    }

    public SimulationStats getStats() {
        int animalCount = animals.size();
        int grassCount = map.getGrasses().size();
        int emptySquareCount = map.getEmptySquareCount();
        Map<Genome, Integer> genomeIntegerHashMap = Collections.unmodifiableMap(genomePopularity);
        double averageEnergy = animals.stream().mapToDouble(Animal::getEnergy).average().orElse(0);
        double averageLifeSpan = deadAnimals.stream().mapToDouble(Animal::getLifeSpan).average().orElse(0);
        double averageChildrenCount = animals.stream().mapToDouble(Animal::getChildrenCount).average().orElse(0);
        return new SimulationStats(day, animalCount, grassCount, emptySquareCount, genomeIntegerHashMap, averageEnergy, averageLifeSpan, averageChildrenCount);
    }

    public List<Vector2d> getPreferredPositions() {
        return grassGenerator.getPreferred();
    }

    public List<Animal> getPopularGenome() {
        Optional<Genome> bestGenome = genomePopularity.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
        if (bestGenome.isEmpty()) {
            return List.of();
        }
        return animals.stream().filter(animal -> animal.getGenome().equals(bestGenome.get())).toList();
    }
}