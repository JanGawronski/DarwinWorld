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

    public static Genome breedGenome(Genome left, int leftEnergy, Genome right, int rightEnergy, GeneArrayMutator mutator) {
        if (left.length() != right.length())
            throw new IllegalArgumentException("Genomes have to be the same length");
        if (leftEnergy <= 0 || rightEnergy <= 0)
            throw new IllegalArgumentException("Energy of the parents must be positive");

        if (ThreadLocalRandom.current().nextBoolean()) {
            Genome temp = left;
            left = right;
            right = temp;
        }

        int n = left.length();
        int divide = n * leftEnergy / (leftEnergy + rightEnergy);

        int[] geneArray = new int[n];
        for (int i = 0; i < divide; i++) {
            geneArray[i] = left.get(i);
        }
        for (int i = divide; i < n; i++)
            geneArray[i] = right.get(i);

        mutator.mutate(geneArray);
        return new Genome(geneArray);
    }

    public static Genome randomGenome(int length) {
        int[] array = new int[length];
        for (int i = 0; i < length; i++)
            array[i] = ThreadLocalRandom.current().nextInt(8);
        return new Genome(array);
    }

    public static Character geneToChar(int gene) {
        return switch (gene) {
            case 0 -> '↑';
            case 1 -> '↗';
            case 2 -> '→';
            case 3 -> '↘';
            case 4 -> '↓';
            case 5 -> '↙';
            case 6 -> '←';
            case 7 -> '↖';
            default -> throw new IllegalArgumentException("Gene must be in the range [0-7]");
        };
    }

    @Override
    public String toString() {
        if (genes == null || genes.length == 0)
            return "No genes available";
        StringBuilder sb = new StringBuilder();
        for (int gene : genes)
            sb.append(geneToChar(gene)).append(' ');
        if (sb.length() > 0)
            sb.setLength(sb.length() - 1);
        return sb.toString();
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