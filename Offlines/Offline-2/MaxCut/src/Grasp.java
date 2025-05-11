import java.util.*;

public class Grasp {
    private final SemiGreedyHeuristic semiGreedy;
    private final LocalSearch localSearch;
    private final GreedyHeuristic greedy;
    private final RandomizedHeuristic randomized;

    public Grasp() {
        this.semiGreedy = new SemiGreedyHeuristic();
        this.localSearch = new LocalSearch();
        this.greedy = new GreedyHeuristic();
        this.randomized = new RandomizedHeuristic();
    }

    public static class GraspResult {
        public double randomizedCutValue;
        public double greedyCutValue;
        public double semiGreedyCutValue;
        public double localSearchCutValue;
        public int localSearchIterations;
        public double graspCutValue;
        public int graspIterations;

        public GraspResult(double randomizedCutValue, double greedyCutValue, double semiGreedyCutValue,
                           double localSearchCutValue, int localSearchIterations, double graspCutValue, int graspIterations) {
            this.randomizedCutValue = randomizedCutValue;
            this.greedyCutValue = greedyCutValue;
            this.semiGreedyCutValue = semiGreedyCutValue;
            this.localSearchCutValue = localSearchCutValue;
            this.localSearchIterations = localSearchIterations;
            this.graspCutValue = graspCutValue;
            this.graspIterations = graspIterations;
        }
    }

    public GraspResult calculateGrasp(Graph graph, double alpha, int numIterations, int randomizedIterations) {
        // Randomized Heuristic
        double randomizedCutValue = randomized.calculateRandomizedHeuristic(graph, randomizedIterations);

        // Greedy Heuristic
        double greedyCutValue = greedy.calculateGreedyHeuristic(graph);

        // Semi-Greedy Heuristic
        SemiGreedyResult semiGreedyResult = semiGreedy.calculateSemiGreedy(graph, alpha);
        Set<Integer> partitionX = semiGreedyResult.partitionX;
        Set<Integer> partitionY = semiGreedyResult.partitionY;
        double semiGreedyCutValue = semiGreedyResult.cutValue;

        // Local Search (using Semi-Greedy partitions)
        LocalSearch.LocalSearchResult localSearchResult = localSearch.localSearch(graph, partitionX, partitionY);
        double localSearchCutValue = localSearchResult.cutValue;
        int localSearchIterations = localSearchResult.iterations;

        // GRASP
        /**
         * Semi-Greedy heuristic introduces randomness (via the Restricted Candidate List and the alpha parameter), allowing different solutions in each iteration.
         * The Greedy heuristic, on the other hand, is deterministic. Given the same graph, it always produces the same partitions and
         * cut value because it selects the vertex with the maximum contribution to the cut at each step without randomness.
         * Iterating the Greedy heuristic multiple times would yield identical results, making iteration unnecessary.
         */
        double bestCutValue = Double.MIN_VALUE;
        for (int i = 0; i < numIterations; i++) {
            SemiGreedyResult iterResult = semiGreedy.calculateSemiGreedy(graph, alpha);
            double currentCutValue = localSearch.localSearch(graph, iterResult.partitionX, iterResult.partitionY).cutValue;
            if (currentCutValue > bestCutValue) {
                bestCutValue = currentCutValue;
            }
        }

        return new GraspResult(randomizedCutValue, greedyCutValue, semiGreedyCutValue,
                localSearchCutValue, localSearchIterations, bestCutValue, numIterations);
    }
}