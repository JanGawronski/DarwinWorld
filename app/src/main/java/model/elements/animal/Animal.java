package model.elements.animal;

import model.MapDirection;
import model.Vector2d;
import model.elements.WorldElement;
import model.elements.animal.geneselectors.NextGeneSelector;
import model.map.MoveConverter;
import model.util.Pair;

import java.util.concurrent.ThreadLocalRandom;

public class Animal implements WorldElement {
    private final Genome genome;
    private int energy;
    private MapDirection orientation;
    private Vector2d position;
    private int activeGene;

    public Animal(int startingEnergy, Genome genome, int startingGene, MapDirection orientation, Vector2d position) {
        if (startingGene >= genome.length() || startingGene < 0)
            throw new IllegalArgumentException("Starting gene is out of bounds");
        this.energy = startingEnergy;
        this.genome = genome;
        this.activeGene = startingGene;
        this.orientation = orientation;
        this.position = position;
    }

    public Animal(int startingEnergy, Genome genome, Vector2d position) {
        this(startingEnergy, genome, ThreadLocalRandom.current().nextInt(0, genome.length()), MapDirection.randomDirection(), position);
    }


    public static Animal breed(Animal father, Animal mother, int energyTransferred, GeneArrayMutator mutator) {
        if (father.position != mother.position)
            throw new IllegalArgumentException("Parents must have the same position");
        if (father.energy < energyTransferred || mother.energy < energyTransferred)
            throw new IllegalArgumentException("Parents must have enough energy to give up");


        Genome childGenome = Genome.breedGenome(father, mother, mutator);
        father.energy -= energyTransferred;
        mother.energy -= energyTransferred;
        return new Animal(2 * energyTransferred, childGenome, father.position);
    }

    public void move(MoveConverter converter, NextGeneSelector selector) {
        energy--;

        MapDirection newOrientation = orientation.rotated(genome.get(activeGene));
        Vector2d newPosition = position.add(newOrientation.toMovementVector());

        Pair<Vector2d, MapDirection> newMove = converter.convert(newPosition, newOrientation);
        this.position = newMove.first();
        this.orientation = newMove.second();

        activeGene = selector.nextGene(genome, activeGene);
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
        return this.orientation.toString();
    }
}
