package model.elements.animal.geneselectors;

import model.elements.animal.Genome;

public interface NextGeneSelector {
    int nextGene(Genome genome, int currentGene);
}
