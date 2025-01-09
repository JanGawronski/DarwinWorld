package simulation;
import model.AnimalConfigData;
import model.Vector2d;
import model.elements.animal.Animal;
import model.elements.animal.Genome;
import model.elements.animal.ParentNotSaturatedException;
import model.elements.grass.Grass;
import model.elements.grass.generators.CreepingJungle;
import model.elements.grass.generators.ForestedEquators;
import model.elements.grass.generators.GrassGenerator;
import model.map.WorldMap;

import java.util.*;
import java.util.concurrent.*;


public class Simulation implements Runnable {
    private final Set<Animal> animals = Collections.synchronizedSet(new HashSet<>());
    private final Set<Animal> deadAnimals = Collections.synchronizedSet(new HashSet<>());
    private final WorldMap map;
    private final GrassGenerator grassGenerator;
    private final int grassGrowthRate;
    private final ConcurrentHashMap<Genome, Integer> genomePopularity = new ConcurrentHashMap<>();
    private int day = 0;
    private ScheduledExecutorService executor;
    private volatile int speed = 1;
    private final List<SimulationChangeListener> listeners = new ArrayList<>();

    public Simulation(WorldMap map, AnimalConfigData animalConfigData, boolean defaultGrassGenerator,
                      int initialGrassCount, int grassGrowthRate, int initialAnimalCount, int initialEnergy) {
        this.map = map;
        this.grassGenerator = defaultGrassGenerator ? new ForestedEquators(map) : new CreepingJungle(map);
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

    public Simulation(SimulationConfig config) {
        this(config.map(), config.animalConfigData(), config.defaultGrassGenerator(), config.initialGrassCount(),
                config.grassGrowthRate(), config.initialAnimalCount(), config.initialEnergy());
    }

    @Override
    public void run() {
        removeDeadAnimals();
        moveAnimals();
        eatGrass();
        breedAnimals();
        growGrass();
        day++;
        notifyListeners();
    }

    private boolean isRunning() {
        return executor != null && !executor.isShutdown();
    }

    public void start() {
        if (isRunning())
            throw new IllegalStateException("Simulation is not stopped");
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this, 500 / speed, 1000 / speed, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    public void setSpeed(int speed) {
        this.speed = speed;
        if (isRunning()) {
            executor.shutdownNow();
            start();
        }
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
        for (Vector2d position : map.getAnimalPositions()) {
            Set<Animal> positionAnimals = map.getAnimalsAt(position);
            if (positionAnimals.size() < 2) {
                continue;
            }
            Animal first = Collections.max(positionAnimals);
            Animal second = Collections.max(positionAnimals.stream().filter(animal -> animal != first).toList());
            try {
                Animal child = Animal.breed(first, second);
                animals.add(child);
                genomePopularity.merge(child.getGenome(), 1, Integer::sum);
                map.place(child);
            } catch (ParentNotSaturatedException e) {
                // not enough energy in parents, no child
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
        int animalCount;
        double averageEnergy, averageChildrenCount;
        synchronized (animals) {
            animalCount = animals.size();
            averageEnergy = animals.stream().mapToDouble(Animal::getEnergy).average().orElse(0);
            averageChildrenCount = animals.stream().mapToDouble(Animal::getChildrenCount).average().orElse(0);
        }
        double averageLifeSpan;
        synchronized (deadAnimals) {
            averageLifeSpan = deadAnimals.stream().mapToDouble(Animal::getLifeSpan).average().orElse(0);
        }
        int grassCount = map.getGrassesPositions().size();
        int emptySquareCount = map.getEmptySquareCount();
        Map<Genome, Integer> genomeIntegerHashMap = Collections.unmodifiableMap(genomePopularity);
        return new SimulationStats(day, animalCount, grassCount, emptySquareCount, genomeIntegerHashMap, averageEnergy, averageLifeSpan, averageChildrenCount);
    }

    public List<Vector2d> getPreferredPositions() {
        return grassGenerator.getPreferred();
    }

    public List<Animal> getPopularAnimals() {
        Optional<Genome> bestGenome = genomePopularity.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
        return bestGenome.map(genome -> animals.stream().filter(animal -> animal.getGenome().equals(genome)).toList()).orElseGet(List::of);
    }

    public WorldMap getMap() {
        return map;
    }
    
    private void notifyListeners() {
        listeners.forEach(listener -> listener.simulationChanged(this));
    }

    public void addListener(SimulationChangeListener listener) {
        listeners.add(listener);
    }
}