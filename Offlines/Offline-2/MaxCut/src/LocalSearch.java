import java.util.*;

public class LocalSearch {
    public static class LocalSearchResult {
        public double averageCutValue;
        public int iterations;

        public LocalSearchResult(double averageCutValue, int iterations) {
            this.averageCutValue = averageCutValue;
            this.iterations = iterations;
        }
    }

    public LocalSearchResult localSearch(Graph graph, int k, int maxDepth) {
        Random random = new Random();
        double totalCutValue = 0.0;
        double totalIterations = 0.0;

        // Run Local Search k times with different random initial solutions
        for (int i = 0; i < k; i++) {
            // Generate random initial solution
            Set<Integer> partitionX = new HashSet<>();
            Set<Integer> partitionY = new HashSet<>();
            for (int v = 1; v <= graph.numVertices; v++) {
                if (random.nextBoolean()) {
                    partitionX.add(v);
                } else {
                    partitionY.add(v);
                }
            }

            // Run Local Search with maxDepth
            SingleResult result = localSearchSingle(graph, partitionX, partitionY, maxDepth);
            totalCutValue += result.cutValue;
            totalIterations += result.iterations;
        }

        // Compute averages
        double averageCutValue = k > 0 ? totalCutValue / k : 0.0;
        int averageIterations = k > 0 ? (int) Math.round(totalIterations/k ) : 0;
        return new LocalSearchResult(averageCutValue, averageIterations);
    }

    public static class SingleResult {
        double cutValue;
        int iterations;

        SingleResult(double cutValue, int iterations) {
            this.cutValue = cutValue;
            this.iterations = iterations;
        }
    }

    public SingleResult localSearchSingle(Graph graph, Set<Integer> initialPartitionX, Set<Integer> initialPartitionY, int maxDepth) {
        Set<Integer> partitionX = new HashSet<>(initialPartitionX);
        Set<Integer> partitionY = new HashSet<>(initialPartitionY);
        int depth = 0;
        boolean improved;

        do {
            improved = false;
            int bestVertex = -1;
            double bestDelta = 0;
            boolean moveToX = false;

            for (int v = 1; v <= graph.numVertices; v++) {
                partitionCut cut = graph.getCutContribution(v, partitionX, partitionY);
                double delta = partitionX.contains(v) ? cut.sigmaY - cut.sigmaX : cut.sigmaX - cut.sigmaY;
                if (delta > bestDelta) {
                    bestDelta = delta;
                    bestVertex = v;
                    moveToX = partitionY.contains(v);
                    improved = true;
                }
            }

            if (improved && depth < maxDepth) {
                if (moveToX) {
                    partitionY.remove(bestVertex);
                    partitionX.add(bestVertex);
                } else {
                    partitionX.remove(bestVertex);
                    partitionY.add(bestVertex);
                }
                depth++;

            } else {
                break;
            }
        } while (improved && depth < maxDepth);

        return new SingleResult(graph.totalCutValue(partitionX, partitionY), depth);
    }
}