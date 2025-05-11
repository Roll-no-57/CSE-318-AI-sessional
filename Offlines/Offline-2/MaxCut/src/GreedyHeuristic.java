
import java.util.*;

public class GreedyHeuristic {
    public double calculateGreedyHeuristic(Graph graph) {
        Set<Integer> partitionX = new HashSet<>();
        Set<Integer> partitionY = new HashSet<>();
        Set<Integer> remainingVertices = new HashSet<>();

        // Initialize remainingVertices with all vertices
        for (int v = 1; v <= graph.numVertices; v++) {
            remainingVertices.add(v);
        }

        // Step-1: Select the initial edge
        if (graph.maxEdge == null) graph.findCriticalEdges();
        Edge initialEdge = graph.maxEdge;


        if (initialEdge == null || initialEdge.from == 0) {
            // Handle empty graph: arbitrarily assign vertices
            if (!remainingVertices.isEmpty()) {
                int v = remainingVertices.iterator().next();
                partitionX.add(v);
                remainingVertices.remove(v);
            }
            while (!remainingVertices.isEmpty()) {
                int v = remainingVertices.iterator().next();
                partitionY.add(v);
                remainingVertices.remove(v);
            }
            return graph.totalCutValue(partitionX, partitionY);
        }

        // Insert initial edge's vertices
        partitionX.add(initialEdge.from);
        partitionY.add(initialEdge.to);
        remainingVertices.remove(initialEdge.from);
        remainingVertices.remove(initialEdge.to);

        // Step-2: Assign remaining vertices
        while (!remainingVertices.isEmpty()) {
            Map<Integer, partitionCut> cutValues = new HashMap<>();
            int maxVertex = -1;
            double maxVertexWeight = Double.MIN_VALUE;

            for (int v : remainingVertices) {
                partitionCut cut = graph.getCutContribution(v, partitionX, partitionY);
                cutValues.put(v, cut);
                double weight = Math.max(cut.sigmaX, cut.sigmaY);
                if (weight > maxVertexWeight) {
                    maxVertex = v;
                    maxVertexWeight = weight;
                }
            }

            // Handle case where no valid vertex is found
            if (maxVertex == -1) {
                // Arbitrarily assign to partitionY if no contribution
                maxVertex = remainingVertices.iterator().next();
                partitionY.add(maxVertex);
                remainingVertices.remove(maxVertex);
                continue;
            }

            // Add chosen vertex to partition X or Y
            partitionCut chosenCut = cutValues.get(maxVertex);
            if (chosenCut.sigmaX >= chosenCut.sigmaY) {
                partitionX.add(maxVertex);
            } else {
                partitionY.add(maxVertex);
            }

            remainingVertices.remove(maxVertex);
        }

        // Calculate and return the final cut value
        return graph.totalCutValue(partitionX, partitionY);
    }
}




// ================================================ A different implementation ===========================================================================
//import java.util.*;
//
//public class GreedyHeuristic {
//    public double calculateGreedyHeuristic(Graph graph) {
//        // Initialize partitions
//        boolean[] partitionX = new boolean[graph.numVertices + 1];
//        boolean[] partitionY = new boolean[graph.numVertices + 1];
//        Arrays.fill(partitionX, false);
//        Arrays.fill(partitionY, false);
//
//        // Step-1: Select the initial edge with maximum weight
//        if (graph.maxEdge == null) {
//            graph.findCriticalEdges();
//        }
//        Edge maxWeightEdge = graph.maxEdge;
//
//        // Handle empty graph or null maxEdge
//        if (maxWeightEdge == null) {
//            // Arbitrarily assign vertices: first to X, rest to Y
//            if (graph.numVertices >= 1) {
//                partitionX[1] = true;
//                for (int i = 2; i <= graph.numVertices; i++) {
//                    partitionY[i] = true;
//                }
//            }
//            return graph.cutWeight(partitionX); // Should be 0 for empty graph
//        }
//
//        // Place one vertex to each partition
//        partitionX[maxWeightEdge.from] = true;
//        partitionY[maxWeightEdge.to] = true;
//
//        // Step-2: Assign remaining vertices based on contribution
//        for (int z = 1; z <= graph.numVertices; z++) {
//            if (!partitionX[z] && !partitionY[z]) {
//                partitionCut cut = graph.getCutContribution(z, new HashSet<>(Set.of(maxWeightEdge.from)), new HashSet<>(Set.of(maxWeightEdge.to)));
//                double weightX = cut.sigmaX; // Contribution to partitionY (edges to Y)
//                double weightY = cut.sigmaY; // Contribution to partitionX (edges to X)
//
//                if (weightX > weightY) {
//                    partitionX[z] = true;
//                } else {
//                    partitionY[z] = true; // Assign to Y if equal or greater
//                }
//            }
//        }
//
//        // Calculate and return the cut value
//        return graph.cutWeight(partitionX);
//    }
//}


