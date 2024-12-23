package simulation;

import model.elements.animal.Genome;
import java.util.Map;

public record SimulationStats(int day, int animalsCount, int grassCount, int emptySquareCount, Map<Genome, Integer> genomePopularity, double averageEnergy, double averageLifeSpan, double averageChildrenCount) {
    
}
