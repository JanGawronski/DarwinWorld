package model.elements;

import model.MapDirection;
import model.Vector2d;
import model.elements.geneselector.NextGeneSelector;

import java.util.concurrent.ThreadLocalRandom;

public class Animal implements WorldElement {
    private int energy;
    private final int[] genome;
    private int activeGene;

    private final NextGeneSelector selector;
    private MapDirection orientation;
    private Vector2d position;

    public Animal(int startingEnergy, int[] genome, NextGeneSelector selector, MapDirection orientation, Vector2d position) {
        this.energy = startingEnergy;
        this.genome = genome;
        this.activeGene = ThreadLocalRandom.current().nextInt(0, this.genome.length);
        this.selector = selector;
        this.orientation = orientation;
        this.position = position;
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

    public int[] getGenome() {
        return genome;
    }

    @Override
    public String toString() {
        return this.orientation.toString();
    }
}
