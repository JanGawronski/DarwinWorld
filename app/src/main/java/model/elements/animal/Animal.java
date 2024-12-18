package model.elements.animal;

import model.AnimalConfigData;
import model.MapDirection;
import model.Pair;
import model.Vector2d;
import model.elements.WorldElement;
import model.map.MoveConverter;

import java.util.concurrent.ThreadLocalRandom;

public class Animal implements WorldElement {
    private final AnimalConfigData config;
    private final Genome genome;
    private int activeGene;
    private int energy;
    private MapDirection orientation;
    private Vector2d position;
    private final Pair<Animal, Animal> parents;
    private int grassEaten = 0;
    private int childCount = 0;
    private int descendantCount = 0;
    private int liveSpan = 0;
    private Integer deathDay = null;
    private Integer latestBreedId = null;

    public Animal(AnimalConfigData config, int startingEnergy, Genome genome, int startingGene, MapDirection orientation, Vector2d position, Pair<Animal, Animal> parents) {
        if (startingGene >= genome.length() || startingGene < 0)
            throw new IllegalArgumentException("Starting gene is out of bounds");
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

    public static Animal breed(Animal father, Animal mother, int breedId) {
        if (father.position != mother.position)
            throw new IllegalArgumentException("Parents must have the same position");
        if (father.config != mother.config)
            throw new IllegalArgumentException("Parents must have the same configuration");

        Genome childGenome = Genome.breedGenome(mother.genome, mother.energy, father.genome, father.energy, mother.config.mutator());
        father.childCount++;
        mother.childCount++;
        mother.addDescendant(breedId);
        father.addDescendant(breedId);
        father.energy -= father.config.birthEnergy();
        mother.energy -= mother.config.birthEnergy();
        return new Animal(mother.config, father.config.birthEnergy() + mother.config.birthEnergy(), childGenome, father.position, new Pair<>(mother, father));
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

    @Override
    public String toString() {
        return orientation.toString();
    }

    private void addDescendant(int breedNumber) {
        if(breedNumber == this.latestBreedId)
            return;
        this.latestBreedId = breedNumber;
        descendantCount++;
        if(parents == null)
            return;
        parents.first().addDescendant(breedNumber);
        parents.second().addDescendant(breedNumber);
    }

    public void setDeathDay(int deathDay) {
        if(this.deathDay != null)
            throw new AlreadyDeadException("Animal is already dead");
        if(deathDay < 0)
            throw new IllegalArgumentException("Time of death cannot be negative");
        this.deathDay = deathDay;
    }

    public AnimalStats getStats() {
        return new AnimalStats(genome, activeGene, energy, grassEaten, childCount, descendantCount, liveSpan, deathDay);
    }
}
