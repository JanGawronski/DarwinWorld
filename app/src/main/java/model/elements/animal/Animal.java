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

    public Animal(AnimalConfigData config, int startingEnergy, Genome genome, int startingGene, MapDirection orientation, Vector2d position) {
        if (startingGene >= genome.length() || startingGene < 0)
            throw new IllegalArgumentException("Starting gene is out of bounds");
        this.config = config;
        this.energy = startingEnergy;
        this.genome = genome;
        this.activeGene = startingGene;
        this.orientation = orientation;
        this.position = position;
    }

    public Animal(AnimalConfigData config, int startingEnergy, Genome genome, Vector2d position) {
        this(config, startingEnergy, genome, ThreadLocalRandom.current().nextInt(genome.length()), MapDirection.randomDirection(), position);
    }

    public static Animal breed(Animal father, Animal mother) {
        if (father.position != mother.position)
            throw new IllegalArgumentException("Parents must have the same position");
        if (father.config != mother.config)
            throw new IllegalArgumentException("Parents must have the same configuration");


        Genome childGenome = Genome.breedGenome(mother.genome, mother.energy, father.genome, father.energy, mother.config.mutator());
        father.energy -= father.config.birthEnergy();
        mother.energy -= mother.config.birthEnergy();
        return new Animal(mother.config, father.config.birthEnergy() + mother.config.birthEnergy(), childGenome, father.position);
    }

    public void feed() {
        energy += config.feedEnergy();
    }

    public void move(MoveConverter converter) {
        energy--;

        MapDirection newOrientation = orientation.rotated(genome.get(activeGene));
        Vector2d newPosition = position.add(newOrientation.toMovementVector());

        Pair<Vector2d, MapDirection> newMove = converter.convert(newPosition, newOrientation);
        this.position = newMove.first();
        this.orientation = newMove.second();

        activeGene = this.config.selector().nextGene(genome, activeGene);
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
}
