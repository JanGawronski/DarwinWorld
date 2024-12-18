package model.elements.animal.geneselectors;

public abstract class NextGeneSelector {
    protected final int genomeLength;

    public NextGeneSelector(int genomeLength) {
        if (genomeLength <= 0)
            throw new IllegalArgumentException("Genome length must be positive");
        this.genomeLength = genomeLength;
    }

    public int getGenomeLength() {
        return genomeLength;
    }

    public abstract int nextGene(int currentGene);
}
