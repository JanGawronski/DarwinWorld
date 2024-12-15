import model.elements.animal.geneselectors.NextGeneSelector;
import model.map.WorldMap;

public class Simulation {
    private final NextGeneSelector selector;
    private final WorldMap map;

    public Simulation(NextGeneSelector selector, WorldMap map) {
        this.selector = selector;
        this.map = map;
    }
}
