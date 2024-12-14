package model.elements.animal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class GenomeTest {

    @Test
    void breedGenome() {
        GeneArrayMutator mutator = new GeneArrayMutator(0, 0, 6);
        Genome g1 = new Genome(new int[]{0, 0, 0, 0, 0, 0});
        Genome g2 = new Genome(new int[]{1, 1, 1, 1, 1, 1});
        Animal a1 = new Animal(10, g1);
        Animal a2 = new Animal(5, g2);
        Genome r1 = new Genome(new int[]{0, 0, 0, 0, 1, 1});
        Genome r2 = new Genome(new int[]{1, 1, 0, 0, 0, 0});

        Genome g3 = Genome.breedGenome(a1, a2, mutator);

        if (!g3.equals(r1) && !g3.equals(r2))
            fail();
    }
}