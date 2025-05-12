import java.util.*;

public class GreedyHeuristic {
    public double calculateGreedyHeuristic(Graph graph) {
        Set<Integer> partitionX = new HashSet<>();
        Set<Integer> partitionY = new HashSet<>();
        Set<Integer> unassignedVertices = new HashSet<>();

        // Initialize unassignedVertices with all vertices
        for (int v = 1; v <= graph.numVertices; v++) {
            unassignedVertices.add(v);
        }

        // Find edge with maximum weight
        if (graph.maxEdge == null) graph.findCriticalEdges();
        Edge maxEdge = graph.maxEdge;

        // Handle edge cases (no edges or empty graph)
        if (maxEdge == null || maxEdge.from == 0) {
            // Arbitrarily assign vertices if no valid edge exists
            if (!unassignedVertices.isEmpty()) {
                int v = unassignedVertices.iterator().next();
                partitionX.add(v);
                unassignedVertices.remove(v);
            }
            while (!unassignedVertices.isEmpty()) {
                int v = unassignedVertices.iterator().next();
                partitionY.add(v);
                unassignedVertices.remove(v);
            }
            return graph.totalCutValue(partitionX, partitionY);
        }

        // Assign vertices of max edge to partitions
        partitionX.add(maxEdge.from);
        partitionY.add(maxEdge.to);
        unassignedVertices.remove(maxEdge.from);
        unassignedVertices.remove(maxEdge.to);

        // Process remaining vertices one by one
        while (!unassignedVertices.isEmpty()) {
            int nextVertex = unassignedVertices.iterator().next();
            unassignedVertices.remove(nextVertex);

            // Calculate contribution to cut if placed in X or Y
            double wX = 0; // weight if placed in X (sum of weights to Y)
            double wY = 0; // weight if placed in Y (sum of weights to X)

            // Calculate wX (sum of weights between nextVertex and vertices in Y)
            for (int y : partitionY) {
                Integer weight = graph.adjList.get(nextVertex).get(y);
                if (weight != null) {
                    wX += weight;
                }
            }

            // Calculate wY (sum of weights between nextVertex and vertices in X)
            for (int x : partitionX) {
                Integer weight = graph.adjList.get(nextVertex).get(x);
                if (weight != null) {
                    wY += weight;
                }
            }

            // Assign vertex to the partition that maximizes the cut
            if (wX > wY) {
                partitionX.add(nextVertex);
            } else {
                partitionY.add(nextVertex);
            }
        }

        // Calculate and return the final cut value
        return graph.totalCutValue(partitionX, partitionY);
    }
}