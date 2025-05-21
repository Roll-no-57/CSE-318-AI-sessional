import java.io.*;
import java.util.*;

public class MaxCutSolver {
    public static void main(String[] args) {
        // Parameters
        double alpha = 0.5;
        int randomizedIterations = 100;
        int localSearchK = 5; // Number of LS initial solutions
//        long seed = 42;
        String studentId = "2105057_3"; // Replace with your student ID
        String csvFile = studentId + ".csv";

        // Known best solutions
        Map<String, Integer> knownBestSolutions = new HashMap<>();
        knownBestSolutions.put("G1", 12078); knownBestSolutions.put("G2", 12084); knownBestSolutions.put("G3", 12077);
        knownBestSolutions.put("G11", 627); knownBestSolutions.put("G12", 621); knownBestSolutions.put("G13", 645);
        knownBestSolutions.put("G14", 3187); knownBestSolutions.put("G15", 3169); knownBestSolutions.put("G16", 3172);
        knownBestSolutions.put("G22", 14123); knownBestSolutions.put("G23", 14129); knownBestSolutions.put("G24", 14131);
        knownBestSolutions.put("G32", 1560); knownBestSolutions.put("G33", 1537); knownBestSolutions.put("G34", 1541);
        knownBestSolutions.put("G35", 8000); knownBestSolutions.put("G36", 7996); knownBestSolutions.put("G37", 8009);
        knownBestSolutions.put("G43", 7027); knownBestSolutions.put("G44", 7022); knownBestSolutions.put("G45", 7020);
        knownBestSolutions.put("G48", 6000); knownBestSolutions.put("G49", 6000); knownBestSolutions.put("G50", 5988);

        // Initialize CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("Name,|V|,|E|,Simple Randomized,Simple Greedy,Semi-greedy,Simple local No. of iterations,Simple local Average value,GRASP No. of iterations,GRASP Best value,Known best solution or upper bound");
        } catch (IOException e) {
            System.err.println("Error initializing CSV: " + e.getMessage());
            return;
        }

        // Process 54 benchmark graphs
//        String inputDir = "path/to/benchmark/graphs/"; // Replace with actual path
        for (int i = 1; i <= 54; i++) {
            String problemName = "G" + i;
            String filePath = "set1/g" + i + ".rud";
            try {
                // Read graph
                Graph graph = readGraph(filePath);
                Grasp grasp = new Grasp();

                // Adjust parameters for large graphs
                int graspIterations = graph.numVertices > 1000 || graph.numEdges > 20000 ? 2 : 50;
                int localSearchDepth = graph.numVertices > 1000 || graph.numEdges > 20000 ? 2 : 100;

                // Run algorithms
                Grasp.GraspResult result = grasp.calculateGrasp(graph, alpha, graspIterations, randomizedIterations, localSearchK, localSearchDepth);

                // Append to CSV
                String knownBest = knownBestSolutions.containsKey(problemName) ? String.valueOf(knownBestSolutions.get(problemName)) : "";
                String[] row = new String[]{
                        problemName,
                        String.valueOf(graph.numVertices),
                        String.valueOf(graph.numEdges),
                        String.format("%.2f", result.randomizedCutValue),
                        String.format("%.2f", result.greedyCutValue),
                        String.format("%.2f", result.semiGreedyCutValue),
                        String.valueOf(result.localSearchIterations),
                        String.format("%.2f", result.localSearchCutValue),
                        String.valueOf(result.graspIterations),
                        String.format("%.2f", result.graspCutValue),
                        knownBest
                };
                appendToCsv(csvFile, row);

                System.out.println("Processed " + problemName);
            } catch (IOException e) {
                System.err.println("Error processing " + problemName + ": " + e.getMessage());
            }
        }

        System.out.println("CSV file generated: " + csvFile);
    }

    private static Graph readGraph(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] firstLine = reader.readLine().trim().split("\\s+");
            int numVertices = Integer.parseInt(firstLine[0]);
            int numEdges = Integer.parseInt(firstLine[1]);
            Graph graph = new Graph(numVertices, numEdges);
            for (int i = 0; i < numEdges; i++) {
                String[] edge = reader.readLine().trim().split("\\s+");
                int v1 = Integer.parseInt(edge[0]);
                int v2 = Integer.parseInt(edge[1]);
                int weight = Integer.parseInt(edge[2]);
                graph.addEdge(v1, v2, weight);
            }
            return graph;
        }
    }

    private static void appendToCsv(String csvFile, String[] row) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile, true))) {
            writer.println(String.join(",", row));
        }
    }
}