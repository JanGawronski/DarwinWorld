package model.elements.animal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class GenomeTest {

    @Test
    void breedGenome() {
        GeneArrayMutator mutator = new GeneArrayMutator(0, 0, 6);
        Genome g1 = new Genome(new int[]{-1, -2, -3, -4, -5, -6});
        Genome g2 = new Genome(new int[]{1, 2, 3, 4, 5, 6});
        Animal a1 = new Animal(10, g1);
        Animal a2 = new Animal(5, g2);
        Genome r1 = new Genome(new int[]{-1, -2, -3, -4, 5, 6});
        Genome r2 = new Genome(new int[]{1, 2, -3, -4, -5, -6});

        for(int i=0;i<10;i++) {
            Genome g3 = Genome.breedGenome(a1, a2, mutator);
            if (!g3.equals(r1) && !g3.equals(r2))
                fail("Resulting genome isn't equal to either of two possibilities");
        }
    }
}