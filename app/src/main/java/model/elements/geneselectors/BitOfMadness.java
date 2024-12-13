package model.elements.geneselectors;

import java.util.concurrent.ThreadLocalRandom;

public class BitOfMadness implements NextGeneSelector {
    @Override
    public int nextGene(int[] genome, int currentGene) {
        if (ThreadLocalRandom.current().nextInt(0, 5) != 0)
            return (currentGene + 1) % genome.length;

        int next = ThreadLocalRandom.current().nextInt(0, genome.length - 1);
        if (next >= currentGene)
            next++;
        return next;
    }
}
