package model.elements.animal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class GenomeTest {

    @Test
    void illegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Genome(new int[]{-1, 4, 5, 6, 7}));
        assertThrows(IllegalArgumentException.class, () -> new Genome(new int[0]));
    }

    @Test
    void breedGenome() {
        GeneArrayMutator mutator = new GeneArrayMutator(0, 0, 6);
        Genome g1 = new Genome(new int[]{0, 0, 1, 1, 2, 2});
        Genome g2 = new Genome(new int[]{3, 3, 4, 4, 5, 5});

        Genome wrongLength = new Genome(new int[]{0, 4, 7});

        Genome r1 = new Genome(new int[]{0, 0, 1, 1, 5, 5});
        Genome r2 = new Genome(new int[]{3, 3, 4, 4, 2, 2});

        for(int i=0;i<10;i++) {
            Genome g3 = Genome.breedGenome(g1, 10, g2, 5, mutator);
            if (!g3.equals(r1) && !g3.equals(r2))
                fail("Resulting genome isn't equal to either of two possibilities");
        }

        assertThrows(IllegalArgumentException.class, () -> Genome.breedGenome(g1, 5, wrongLength, 5, mutator));
        assertThrows(IllegalArgumentException.class, () -> Genome.breedGenome(g1, 0, g2, 5, mutator));
    }
}