import java.util.*;

public class LocalSearch {
    public static class LocalSearchResult {
        public double cutValue;
        public int iterations;
        public LocalSearchResult(double cutValue, int iterations) {
            this.cutValue = cutValue;
            this.iterations = iterations;
        }
    }
    /**
     * Performs local search starting from an initial solution (partitionX and partitionY).
     * Moves vertices between partitions to maximize the cut value until no improvement is possible.
     * @param graph The input graph
     * @param initialPartitionX Initial set of vertices in partition X
     * @param initialPartitionY Initial set of vertices in partition Y
     * @return The final cut value after local search
     */
    public LocalSearchResult localSearch(Graph graph, Set<Integer> initialPartitionX, Set<Integer> initialPartitionY) {
        Set<Integer> partitionX = new HashSet<>(initialPartitionX);
        Set<Integer> partitionY = new HashSet<>(initialPartitionY);

        boolean improved;
        int iterations = 0;

        do {
            improved = false;
            int bestVertex = -1;
            double bestDelta = 0;
            boolean moveToX = false;

            // Evaluate all possible moves
            for (int v = 1; v <= graph.numVertices; v++) {
                partitionCut cut = graph.getCutContribution(v, partitionX, partitionY);
                double delta;

                if (partitionX.contains(v)) {
                    // Move v from X to Y: delta = sigma_Y(v) - sigma_X(v)
                    delta = cut.sigmaY - cut.sigmaX;
                } else {
                    // Move v from Y to X: delta = sigma_X(v) - sigma_Y(v)
                    delta = cut.sigmaX - cut.sigmaY;
                }

                if (delta > bestDelta) {
                    bestDelta = delta;
                    bestVertex = v;
                    moveToX = partitionY.contains(v);
                    improved = true;
                }
            }

            // Perform the best move
            if (improved) {
                if (moveToX) {
                    partitionY.remove(bestVertex);
                    partitionX.add(bestVertex);
                } else {
                    partitionX.remove(bestVertex);
                    partitionY.add(bestVertex);
                }
                iterations++;
            }
        } while (improved);

        // Calculate final cut value
        return new LocalSearchResult(graph.totalCutValue(partitionX,partitionY),iterations);
    }
}