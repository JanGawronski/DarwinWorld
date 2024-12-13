package model.elements.geneselectors;

public class FullPredestination implements NextGeneSelector {
    @Override
    public int nextGene(int[] genome, int currentGene) {
        return (currentGene + 1) % genome.length;
    }
}
