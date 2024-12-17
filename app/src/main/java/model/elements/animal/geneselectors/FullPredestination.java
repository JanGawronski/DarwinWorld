package model.elements.animal.geneselectors;

public class FullPredestination extends NextGeneSelector {
    public FullPredestination(int genomeLength) {
        super(genomeLength);
    }
    @Override
    public int nextGene(int currentGene) {
        if (currentGene >= genomeLength || currentGene < 0)
            throw new IllegalArgumentException("Previous gene was out of bounds");
        return (currentGene + 1) % genomeLength;
    }
}
