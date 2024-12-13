package model.elements.animal;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {
    private final int[] genes;

    public Genome(int[] geneArray) {
        if (geneArray.length == 0)
            throw new IllegalArgumentException("Genome cannot have 0 genes");
        this.genes = geneArray.clone();
    }

    public static Genome breedGenome(Animal left, Animal right) {
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
        return new Genome(geneArray);
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