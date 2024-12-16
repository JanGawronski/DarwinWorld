package model.elements.animal.geneselectors;

import model.elements.animal.Genome;

@FunctionalInterface
public interface NextGeneSelector {
    int nextGene(Genome genome, int currentGene);
}
