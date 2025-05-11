import java.util.*;

public class SemiGreedyHeuristic {
    private Set<Integer> partitionX; // Store partition X
    private Set<Integer> partitionY; // Store partition Y

    /**
     * Calculates the Semi-Greedy heuristic for MAX-CUT and stores the resulting partitions.
     * @param graph The input graph
     * @param alpha The alpha parameter for the Restricted Candidate List
     * @return A SemiGreedyResult containing the cut value and partitions
     */
    public SemiGreedyResult calculateSemiGreedy(Graph graph, double alpha) {
        // Initialize partitions and remaining vertices
        partitionX = new HashSet<>();
        partitionY = new HashSet<>();
        Set<Integer> remainingVertices = new HashSet<>();

        // Initialize the remainingVertices set with all vertices
        for (int v = 1; v <= graph.numVertices; v++) {
            remainingVertices.add(v);
        }

        // Step-1: Select initial edge using semi-greedy method
        Edge initialEdge;

        // SubStep-1: Calculate the threshold value μ
        if (graph.maxEdge == null || graph.minEdge == null) {
            graph.findCriticalEdges();
        }
        int wMin = graph.minEdge.weight;
        int wMax = graph.maxEdge.weight;
        double threshold = wMin + alpha * (wMax - wMin);

        // SubStep-2: Construct a Restricted Candidate List (RCL) for edges
        List<Edge> initialRcl = new ArrayList<>();
        for (int u = 1; u <= graph.numVertices; u++) {
            for (Map.Entry<Integer, Integer> entry : graph.adjList.get(u).entrySet()) {
                int v = entry.getKey();
                int weight = entry.getValue();
                if (u < v && weight >= threshold) {
                    initialRcl.add(new Edge(u, v, weight));
                }
            }
        }

        // SubStep-3: Choose a random edge from the initial RCL
        Random random = new Random();
        if (initialRcl.isEmpty()) {
            // Fallback: Use the max edge if RCL is empty
            initialEdge = graph.maxEdge;
        } else {
            int index = random.nextInt(initialRcl.size());
            initialEdge = initialRcl.get(index);
        }

        // Insert the initial edge's vertices into the partitions
        partitionX.add(initialEdge.from);
        partitionY.add(initialEdge.to);
        remainingVertices.remove(initialEdge.from);
        remainingVertices.remove(initialEdge.to);

        // Step-2: Place the remaining vertices into either partitionX or partitionY
        while (partitionX.size() + partitionY.size() < graph.numVertices) {
            // Calculate greedy function values and construct RCL
            double minSigmaX = Double.MAX_VALUE;
            double minSigmaY = Double.MAX_VALUE;
            double maxSigmaX = Double.MIN_VALUE;
            double maxSigmaY = Double.MIN_VALUE;

            Map<Integer, partitionCut> cutValues = new HashMap<>();
            for (int v : remainingVertices) {
                partitionCut cut = graph.getCutContribution(v, partitionX, partitionY);
                cutValues.put(v, cut);
                minSigmaX = Math.min(minSigmaX, cut.sigmaX);
                minSigmaY = Math.min(minSigmaY, cut.sigmaY);
                maxSigmaX = Math.max(maxSigmaX, cut.sigmaX);
                maxSigmaY = Math.max(maxSigmaY, cut.sigmaY);
            }

            // Calculate threshold for the RCL
            double minWeight = Math.min(minSigmaX, minSigmaY);
            double maxWeight = Math.max(maxSigmaX, maxSigmaY);
            double vertexThreshold = minWeight + alpha * (maxWeight - minWeight);

            // Construct RCL based on greedy function values
            List<Integer> rcl = new ArrayList<>();
            for (int v : remainingVertices) {
                partitionCut cut = cutValues.get(v);
                if (Math.max(cut.sigmaX, cut.sigmaY) >= vertexThreshold) {
                    rcl.add(v);
                }
            }

            // Choose a random vertex from the RCL
            int chosenVertex;
            if (rcl.isEmpty()) {
                // Fallback: Choose the vertex with the highest greedy value
                chosenVertex = remainingVertices.iterator().next();
                double maxValue = Double.MIN_VALUE;
                for (int v : remainingVertices) {
                    partitionCut cut = cutValues.get(v);
                    if (Math.max(cut.sigmaX, cut.sigmaY) > maxValue) {
                        maxValue = Math.max(cut.sigmaX, cut.sigmaY);
                        chosenVertex = v;
                    }
                }
            } else {
                chosenVertex = rcl.get(random.nextInt(rcl.size()));
            }

            // Add chosen vertex to set X or Y based on which cut is greater
            partitionCut chosenCut = cutValues.get(chosenVertex);
            if (chosenCut.sigmaX >= chosenCut.sigmaY) {
                partitionX.add(chosenVertex);
            } else {
                partitionY.add(chosenVertex);
            }

            // Remove the chosen vertex from remaining vertices
            remainingVertices.remove(chosenVertex);
        }

        // Calculate the final cut value
        double finalCutValue = 0;
        for (int u : partitionX) {
            for (int v : partitionY) {
                Integer weight = graph.adjList.get(u).get(v);
                if (weight != null) {
                    finalCutValue += weight;
                }
            }
        }

        // Return the result with cut value and partitions
        return new SemiGreedyResult(finalCutValue, new HashSet<>(partitionX), new HashSet<>(partitionY));
    }

    /**
     * Gets the partition X from the last run
     * @return Set of vertices in partition X
     */
    public Set<Integer> getPartitionX() {
        return new HashSet<>(partitionX);
    }

    /**
     * Gets the partition Y from the last run
     * @return Set of vertices in partition Y
     */
    public Set<Integer> getPartitionY() {
        return new HashSet<>(partitionY);
    }
}

/**
 * Class to store the result of the Semi-Greedy heuristic
 */
class SemiGreedyResult {
    public double cutValue;
    public Set<Integer> partitionX;
    public Set<Integer> partitionY;

    public SemiGreedyResult(double cutValue, Set<Integer> partitionX, Set<Integer> partitionY) {
        this.cutValue = cutValue;
        this.partitionX = partitionX;
        this.partitionY = partitionY;
    }
}