package model.elements.animal;

import model.AnimalConfigData;
import model.MapDirection;
import model.Pair;
import model.Vector2d;
import model.elements.WorldElement;
import model.map.MoveConverter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Animal implements WorldElement {
    private final AnimalConfigData config;
    private final Genome genome;
    private final Pair<Animal, Animal> parents;
    private int activeGene;
    private int energy;
    private MapDirection orientation;
    private Vector2d position;
    private int grassEaten = 0;
    private int childCount = 0;
    private int descendantCount = 0;
    private Integer deathDate = null;
    private Integer latestBreedId = null;
    private int liveSpan = 0;

    public Animal(AnimalConfigData config, int startingEnergy, Genome genome, int startingGene, MapDirection orientation, Vector2d position, Pair<Animal, Animal> parents) {
        if (startingEnergy < 0)
            throw new IllegalArgumentException("Energy cannot be negative");
        if (startingGene >= genome.length() || startingGene < 0)
            throw new IndexOutOfBoundsException("Starting gene is out of bounds");
        this.config = config;
        this.energy = startingEnergy;
        this.genome = genome;
        this.activeGene = startingGene;
        this.orientation = orientation;
        this.position = position;
        this.parents = parents;
    }

    public Animal(AnimalConfigData config, int startingEnergy, Genome genome, Vector2d position, Pair<Animal, Animal> parents) {
        this(config, startingEnergy, genome, ThreadLocalRandom.current().nextInt(genome.length()), MapDirection.randomDirection(), position, parents);
    }

    public Animal(AnimalConfigData config, int startingEnergy, Vector2d position) {
        this(config, startingEnergy, Genome.randomGenome(config.selector().getGenomeLength()), ThreadLocalRandom.current().nextInt(config.selector().getGenomeLength()), MapDirection.randomDirection(), position, null);
    }

    public static Animal breed(Animal father, Animal mother, int breedId) throws ParentNotSaturatedException {
        if (father == mother)
            throw new IllegalArgumentException("Parents be different Animals");
        if (!father.config.equals(mother.config))
            throw new IllegalArgumentException("Parents must have the same configuration");
        AnimalConfigData setup = mother.config;
        if (!father.position.equals(mother.position))
            throw new IllegalArgumentException("Parents must have the same position");
        if (father.energy < setup.saturationEnergy() || mother.energy < setup.saturationEnergy())
            throw new ParentNotSaturatedException();

        Genome childGenome = Genome.breedGenome(mother.genome, mother.energy, father.genome, father.energy, setup.mutator());
        father.childCount++;
        mother.childCount++;
        mother.addDescendant(breedId);
        father.addDescendant(breedId);
        father.energy -= setup.birthEnergy();
        mother.energy -= setup.birthEnergy();
        return new Animal(setup, 2 * setup.birthEnergy(), childGenome, father.position, new Pair<>(mother, father));
    }

    public static List<Animal> sorted(Collection<Animal> animals) {
        return animals.stream()
                .sorted(Comparator.comparingInt((Animal animal) -> animal.energy)
                        .thenComparingInt(animal -> animal.liveSpan)
                        .thenComparingInt(animal -> animal.childCount)
                        .thenComparingInt(animal -> ThreadLocalRandom.current().nextInt()))
                .collect(Collectors.toList()).reversed();
    }

    public void eat() {
        energy += config.feedEnergy();
        grassEaten++;
    }

    public void move(MoveConverter converter) {
        energy--;
        liveSpan++;

        MapDirection newOrientation = orientation.rotated(genome.get(activeGene));
        Vector2d newPosition = position.add(newOrientation.toMovementVector());

        Pair<Vector2d, MapDirection> newMove = converter.convert(newPosition, newOrientation);
        this.position = newMove.first();
        this.orientation = newMove.second();

        activeGene = this.config.selector().nextGene(activeGene);
    }

    @Override
    public boolean isAt(Vector2d otherPosition) {
        return this.position.equals(otherPosition);
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public Genome getGenome() {
        return genome;
    }

    @Override
    public String toString() {
        return orientation.toString();
    }

    private void addDescendant(int breedNumber) {
        if (latestBreedId != null && breedNumber == latestBreedId)
            return;
        latestBreedId = breedNumber;
        descendantCount++;
        if (parents == null)
            return;
        parents.first().addDescendant(breedNumber);
        parents.second().addDescendant(breedNumber);
    }

    public boolean isAlive() {
        return deathDate == null;
    }

    public void setDeathDay(int deathDate) {
        if (!isAlive())
            throw new AlreadyDeadException("Animal is already dead");
        if (deathDate < 0)
            throw new IllegalArgumentException("Time of death cannot be negative");
        this.deathDate = deathDate;
    }

    public AnimalStats getStats() {
        return new AnimalStats(genome, activeGene, energy, grassEaten, childCount, descendantCount, liveSpan, Optional.ofNullable(deathDate));
    }
}
