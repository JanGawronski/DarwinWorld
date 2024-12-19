package model.elements.animal;

import java.util.Optional;

public record AnimalStats(Genome genome, int activeGene, int energy, int grassEaten, int children, int descendants,
                          int liveSpan, Optional<Integer> deathDay) {
}
