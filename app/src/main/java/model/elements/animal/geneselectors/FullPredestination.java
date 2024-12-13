package model.elements.animal.geneselectors;

import model.elements.animal.Genome;

public class FullPredestination implements NextGeneSelector {
    @Override
    public int nextGene(Genome genome, int currentGene) {
        if (currentGene >= genome.length() || currentGene < 0)
            throw new IllegalArgumentException("Previous gene was out of bounds");
        return (currentGene + 1) % genome.length();
    }
}
