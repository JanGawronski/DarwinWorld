package model.elements.animal;

import java.util.concurrent.ThreadLocalRandom;

public class GeneArrayMutator {
    private final int min;
    private final int max;
    private final int[] order;

    public GeneArrayMutator(int min, int max, int genomeLength) {
        if (min > max)
            throw new IllegalArgumentException("Min cannot be larger than max");
        if (min < 0 || max > genomeLength)
            throw new IllegalArgumentException("Arguments are out of bounds");
        this.min = min;
        this.max = max;
        this.order = new int[genomeLength];
        for (int i = 0; i < order.length; i++)
            order[i] = i;
    }

    public void mutate(int[] geneArray) {
        if (geneArray.length != order.length)
            throw new IllegalArgumentException("Wrong geneArray length");
        int mutations = ThreadLocalRandom.current().nextInt(min, max + 1);
        int orderIndex, geneToChange;
        for (int i = 0; i < mutations; i++) {
            orderIndex = ThreadLocalRandom.current().nextInt(i, order.length);
            geneToChange = order[orderIndex];
            changeGene(geneArray, geneToChange);
            swapInOrder(i, orderIndex);
        }
    }

    private void changeGene(int[] geneArray, int indexToChange) {
        int newValue = ThreadLocalRandom.current().nextInt(0, 7);
        if (newValue >= geneArray[indexToChange])
            newValue++;
        geneArray[indexToChange] = newValue;
    }

    private void swapInOrder(int a, int b) {
        int temp = order[a];
        order[a] = order[b];
        order[b] = temp;
    }
}
