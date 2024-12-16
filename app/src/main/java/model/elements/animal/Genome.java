package model.elements.animal;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {
    private final int[] genes;

    public Genome(int[] geneArray) {
        if (geneArray.length == 0)
            throw new IllegalArgumentException("Genome cannot have 0 genes");
        for (int gene : geneArray)
            if (gene < 0 || gene > 7)
                throw new IllegalArgumentException("Genes must be integers from the range [0-7]");
        this.genes = geneArray.clone();
    }

    public static Genome breedGenome(Animal left, Animal right, GeneArrayMutator mutator) {
        if (left.getGenome().length() != right.getGenome().length())
            throw new IllegalArgumentException("Genomes have to be the same length");
        if (left.getEnergy() <= 0 || right.getEnergy() <= 0)
            throw new IllegalArgumentException("Energy of the parents must be positive");

        if (ThreadLocalRandom.current().nextBoolean()) {
            Animal temp = left;
            left = right;
            right = temp;
        }

        int n = left.getGenome().length();
        int divide = n * left.getEnergy() / (left.getEnergy() + right.getEnergy());

        int[] geneArray = new int[n];
        for (int i = 0; i < divide; i++) {
            geneArray[i] = left.getGenome().get(i);
        }
        for (int i = divide; i < n; i++)
            geneArray[i] = right.getGenome().get(i);

        mutator.mutate(geneArray);
        return new Genome(geneArray);
    }

    @Override
    public String toString() {
        return "Genome " + Arrays.toString(genes);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Genome genome = (Genome) o;
        return Objects.deepEquals(genes, genome.genes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }

    public int get(int index) {
        return genes[index];
    }

    public int length() {
        return genes.length;
    }
}