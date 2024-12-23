import model.AnimalConfigData;
import model.Vector2d;
import model.elements.animal.Animal;
import model.elements.animal.AnimalStats;
import model.elements.animal.Genome;
import model.elements.animal.ParentNotSaturatedException;
import model.elements.grass.Grass;
import model.elements.grass.generators.GrassGenerator;
import model.map.WorldMap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Simulation implements Runnable {
    private final HashSet<Animal> animals = new HashSet<>();
    private final HashSet<Animal> deadAnimals = new HashSet<>();
    private final WorldMap map;
    private final GrassGenerator grassGenerator;
    //private final AnimalConfigData animalConfig;
    private final int grassGrowthRate;
    private final HashMap<Genome, Integer> genomePopularity = new HashMap<>();
    private int day = 0;
    private ExecutorService executor;
    private volatile int speed = 1;

    public Simulation(WorldMap map, AnimalConfigData animalConfigData, GrassGenerator grassGenerator,
                      int initialGrassCount, int grassGrowthRate, int initialAnimalCount, int initialEnergy) {
        this.map = map;
        this.grassGenerator = grassGenerator;
        this.grassGrowthRate = grassGrowthRate;
        //this.animalConfig = animalConfigData;

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
            Animal animal = Animal.sorted(animalsOnGrass).getFirst();
            animal.eat();
            map.remove(grass);
        }
    }

    private void breedAnimals() {
        Map<Vector2d, HashSet<Animal>> animalsByPosition = map.getAnimalsMap();
        for (HashSet<Animal> positionAnimals : animalsByPosition.values()) {
            if (positionAnimals.size() >= 2) {
                List<Animal> sortedAnimals = Animal.sorted(positionAnimals);
                try {
                    Animal child = Animal.breed(sortedAnimals.get(0), sortedAnimals.get(1), this.animals.size() + this.deadAnimals.size());
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

    public void getStats() { // to są wszystkie potrzebne statystyki
        int animalCount = animals.size();
        int grassCount = map.getGrasses().size();
        int emptySquareCount = map.getEmptySquareCount();
        Map<Genome, Integer> genomeIntegerHashMap = Collections.unmodifiableMap(genomePopularity);
        double averageEnergy = 0;
        double averageLifeSpan = 0;
        double averageLivingKids = 0;
        for (Animal animal : animals) {
            AnimalStats stats = animal.getStats();
            averageEnergy += stats.energy();
            averageLivingKids += stats.children();
        }
        for (Animal animal : deadAnimals) {
            AnimalStats stats = animal.getStats();
            averageLifeSpan += stats.lifeSpan();
        }
        averageEnergy /= animalCount;
        averageLifeSpan /= deadAnimals.size();
        averageLivingKids /= animalCount;
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