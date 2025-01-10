package model.elements.animal;

import model.AnimalConfigData;
import model.MapDirection;
import model.Pair;
import model.Vector2d;
import model.map.MoveConverter;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Animal implements Comparable<Animal> {
    private final AnimalConfigData config;
    private final Genome genome;
    private final Pair<Animal, Animal> parents;
    private final UUID id = UUID.randomUUID();
    private int activeGene;
    private int energy;
    private MapDirection orientation;
    private Vector2d position;
    private int grassEaten = 0;
    private int childCount = 0;
    private int descendantCount = 0;
    private UUID latestDescendantId = null;
    private Integer deathDate = null;
    private int lifeSpan = 0;

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

    public static Animal breed(Animal father, Animal mother) throws ParentNotSaturatedException {
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
        father.energy -= setup.birthEnergy();
        mother.energy -= setup.birthEnergy();
        Animal newborn = new Animal(setup, 2 * setup.birthEnergy(), childGenome, father.position, new Pair<>(mother, father));
        mother.addDescendant(newborn.id);
        father.addDescendant(newborn.id);
        return newborn;
    }

    @Override
    public int compareTo(Animal other) {
        return Comparator.comparingInt((Animal animal) -> animal.energy)
                .thenComparingInt(animal -> animal.lifeSpan)
                .thenComparingInt(animal -> animal.childCount)
                .thenComparing(animal -> animal.id)
                .compare(this, other);
    }

    public void eat() {
        energy += config.feedEnergy();
        grassEaten++;
    }

    public void move(MoveConverter converter) {
        energy--;
        lifeSpan++;

        MapDirection newOrientation = orientation.rotated(genome.get(activeGene));
        Vector2d newPosition = position.add(newOrientation.toMovementVector());

        Pair<Vector2d, MapDirection> newMove = converter.convert(newPosition, newOrientation);
        this.position = newMove.first();
        this.orientation = newMove.second();

        activeGene = this.config.selector().nextGene(activeGene);
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public Genome getGenome() {
        return genome;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public int getChildrenCount() {
        return childCount;
    }

    private void addDescendant(UUID descendantId) {
        if (latestDescendantId != null && latestDescendantId.equals(descendantId))
            return;
        latestDescendantId = descendantId;
        descendantCount++;
        if (parents == null)
            return;
        parents.first().addDescendant(descendantId);
        parents.second().addDescendant(descendantId);
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
        return new AnimalStats(genome, activeGene, energy, grassEaten, childCount, descendantCount, lifeSpan, Optional.ofNullable(deathDate));
    }
}
