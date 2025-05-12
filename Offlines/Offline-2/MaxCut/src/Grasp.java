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

    public GraspResult calculateGrasp(Graph graph, double alpha, int numIterations, int randomizedIterations, int localSearchK, int localSearchDepth) {
        // Randomized Heuristic
        double randomizedCutValue = randomized.calculateRandomizedHeuristic(graph, randomizedIterations);

        // Greedy Heuristic
        double greedyCutValue = greedy.calculateGreedyHeuristic(graph);

        // Semi-Greedy Heuristic
        SemiGreedyResult semiGreedyResult = semiGreedy.calculateSemiGreedy(graph, alpha);
        double semiGreedyCutValue = semiGreedyResult.cutValue;

        // Local Search (k initial solutions)
        LocalSearch.LocalSearchResult localSearchResult = localSearch.localSearch(graph, localSearchK, localSearchDepth);
        double localSearchCutValue = localSearchResult.averageCutValue;
        int localSearchIterations = localSearchResult.iterations;

        // GRASP
        double bestCutValue = Double.MIN_VALUE;
        for (int i = 0; i < numIterations; i++) {
            SemiGreedyResult iterResult = semiGreedy.calculateSemiGreedy(graph, alpha);
            double currentCutValue = localSearch.localSearchSingle(graph, iterResult.partitionX, iterResult.partitionY, localSearchDepth).cutValue;
            if (currentCutValue > bestCutValue) {
                bestCutValue = currentCutValue;
            }
        }

        return new GraspResult(randomizedCutValue, greedyCutValue, semiGreedyCutValue,
                localSearchCutValue, localSearchIterations, bestCutValue, numIterations);
    }
}