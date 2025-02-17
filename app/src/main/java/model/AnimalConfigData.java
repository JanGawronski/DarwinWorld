package model;

import model.elements.animal.GeneArrayMutator;
import model.elements.animal.geneselectors.BitOfMadness;
import model.elements.animal.geneselectors.FullPredestination;
import model.elements.animal.geneselectors.NextGeneSelector;

public record AnimalConfigData(int feedEnergy, int birthEnergy, int saturationEnergy, NextGeneSelector selector,
                               GeneArrayMutator mutator) {
    public AnimalConfigData {
        if (feedEnergy < 0)
            throw new IllegalArgumentException("Energy gained from eating cannot be negative");
        if (birthEnergy <= 0)
            throw new IllegalArgumentException("Child's energy must be positive");
        if (saturationEnergy < 0)
            throw new IllegalArgumentException("Saturation energy must be non-negative");
        if (saturationEnergy < birthEnergy)
            throw new IllegalArgumentException("Saturation energy must be larger than birth energy");
    }

    public AnimalConfigData(int feedEnergy, int birthEnergy, int saturationEnergy, boolean selectorType, int genomeLength, int minMutations, int maxMutations) {
        this(feedEnergy, birthEnergy, saturationEnergy, makeSelector(selectorType, genomeLength), makeMutator(minMutations, maxMutations, genomeLength));
    }

    private static NextGeneSelector makeSelector(boolean selectorType, int genomeLength) {
        if (selectorType)
            return new FullPredestination(genomeLength);
        else
            return new BitOfMadness(genomeLength);
    }

    private static GeneArrayMutator makeMutator(int min, int max, int genomeLength) {
        return new GeneArrayMutator(min, max, genomeLength);
    }
}
