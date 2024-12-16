package model;

import model.elements.animal.GeneArrayMutator;
import model.elements.animal.geneselectors.NextGeneSelector;

public record AnimalConfigData(int feedEnergy, int birthEnergy, NextGeneSelector selector, GeneArrayMutator mutator) {
    public AnimalConfigData {
        if(feedEnergy < 0)
            throw new IllegalArgumentException("Energy gained from eating cannot be negative");
        if(birthEnergy <= 0)
            throw new IllegalArgumentException("Child's energy must be positive");
    }
}
