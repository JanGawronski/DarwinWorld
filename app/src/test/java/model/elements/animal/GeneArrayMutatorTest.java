package model.elements.animal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneArrayMutatorTest {

    @Test
    void illegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> new GeneArrayMutator(5, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new GeneArrayMutator(-1, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new GeneArrayMutator(3, 20, 10));
    }

    @Test
    void mutate() {
        GeneArrayMutator zero = new GeneArrayMutator(0, 0, 10);
        GeneArrayMutator all = new GeneArrayMutator(10, 10, 10);
        GeneArrayMutator medium = new GeneArrayMutator(5, 5, 10);
        int[] original = new int[]{0, 1, 5, 2, 5, 3, 7, 4, 6, 5};
        int[] zeroDiff = original.clone();
        int[] allDiff = original.clone();
        int[] mediumDiff = original.clone();


        zero.mutate(zeroDiff);
        all.mutate(allDiff);
        medium.mutate(mediumDiff);


        assertThrows(IllegalArgumentException.class, () -> medium.mutate(new int[]{1, 2, 3, 4, 5}));

        int seenMin = 17;
        int seenMax = -10;

        int diffCount = 0;
        for (int i=0; i<10;i++) {
            if (original[i] != zeroDiff[i])
                diffCount++;
            seenMin = Math.min(seenMin, zeroDiff[i]);
            seenMax = Math.max(seenMax, zeroDiff[i]);
        }
        assertEquals(0, diffCount);

        diffCount = 0;
        for (int i=0; i<10;i++) {
            if (original[i] != allDiff[i])
                diffCount++;
            seenMin = Math.min(seenMin, allDiff[i]);
            seenMax = Math.max(seenMax, allDiff[i]);
        }
        assertEquals(10, diffCount);

        diffCount = 0;
        for (int i=0; i<10;i++) {
            if (original[i] != mediumDiff[i])
                diffCount++;
            seenMin = Math.min(seenMin, mediumDiff[i]);
            seenMax = Math.max(seenMax, mediumDiff[i]);
        }
        assertEquals(5, diffCount);

        assertEquals(0, Math.min(0, seenMin));
        assertEquals(7, Math.max(7, seenMax));
    }
}