package model.elements.animal.geneselectors;

import java.util.concurrent.ThreadLocalRandom;

public class BitOfMadness extends NextGeneSelector {
    public BitOfMadness(int genomeLength) {
        super(genomeLength);
    }

    @Override
    public int nextGene(int currentGene) {
        if (currentGene >= genomeLength || currentGene < 0)
            throw new IndexOutOfBoundsException("Previous gene was out of bounds");

        if (ThreadLocalRandom.current().nextInt(0, 5) != 0)
            return (currentGene + 1) % genomeLength;

        int nextGene = ThreadLocalRandom.current().nextInt(0, genomeLength - 1);
        if (nextGene >= currentGene)
            nextGene++;
        return nextGene;
    }
}
