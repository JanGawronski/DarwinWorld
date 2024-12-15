import model.elements.animal.GeneArrayMutator;
import model.elements.animal.geneselectors.NextGeneSelector;
import model.map.WorldMap;

public class Simulation {
    private final NextGeneSelector selector;
    private final WorldMap map;
    private final GeneArrayMutator mutator;

    public Simulation(NextGeneSelector selector, WorldMap map, GeneArrayMutator mutator) {
        this.selector = selector;
        this.map = map;
        this.mutator = mutator;
    }
}
