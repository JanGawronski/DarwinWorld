package model.elements.animal;

import model.MapDirection;
import model.Vector2d;
import model.elements.WorldElement;
import model.elements.animal.geneselectors.NextGeneSelector;

import java.util.concurrent.ThreadLocalRandom;

public class Animal implements WorldElement {
    private final Genome genome;
    private final NextGeneSelector selector;
    private final int energy;
    private final MapDirection orientation;
    private final Vector2d position;
    private int activeGene;

    public Animal(int startingEnergy, Genome genome, int startingGene, NextGeneSelector selector, MapDirection orientation, Vector2d position) {
        if (startingGene >= genome.length() || startingGene < 0)
            throw new IllegalArgumentException("Starting gene is out of bounds");
        this.energy = startingEnergy;
        this.genome = genome;
        this.activeGene = startingGene;
        this.selector = selector;
        this.orientation = orientation;
        this.position = position;
    }

    public Animal(int startingEnergy, Genome genome, NextGeneSelector selector, Vector2d position) {
        this(startingEnergy, genome, ThreadLocalRandom.current().nextInt(0, genome.length()), selector, MapDirection.randomDirection(), position);
    }

    public void move() {
        //moving
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
