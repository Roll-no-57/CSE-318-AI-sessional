import java.io.*;
import java.util.*;

public class MaxCutSolver {

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


    public static void main(String[] args) {
        // Parameters (tune as needed)
        double alpha = 0.5; // Semi-Greedy alpha
        int graspIterations = 2; // GRASP iterations
        int randomizedIterations = 100; // Randomized iterations
//        long seed = 42; // Random seed for reproducibility

        // Known best solutions (from assignment)
        Map<String, Integer> knownBestSolutions = new HashMap<>();
        knownBestSolutions.put("g1", 12078); knownBestSolutions.put("g2", 12084); knownBestSolutions.put("g3", 12077);
        knownBestSolutions.put("g11", 627); knownBestSolutions.put("g12", 621); knownBestSolutions.put("g13", 645);
        knownBestSolutions.put("g14", 3187); knownBestSolutions.put("g15", 3169); knownBestSolutions.put("g16", 3172);
        knownBestSolutions.put("g22", 14123); knownBestSolutions.put("g23", 14129); knownBestSolutions.put("g24", 14131);
        knownBestSolutions.put("g32", 1560); knownBestSolutions.put("g33", 1537); knownBestSolutions.put("g34", 1541);
        knownBestSolutions.put("g35", 8000); knownBestSolutions.put("g36", 7996); knownBestSolutions.put("g37", 8009);
        knownBestSolutions.put("g43", 7027); knownBestSolutions.put("g44", 7022); knownBestSolutions.put("g45", 7020);
        knownBestSolutions.put("g48", 6000); knownBestSolutions.put("g49", 6000); knownBestSolutions.put("g50", 5988);

        // CSV output file
        String studentId = "2105057"; // Replace with your student ID
        String csvFile = studentId + ".csv";
        List<String[]> csvRows = new ArrayList<>();
        csvRows.add(new String[]{"Name", "|V|", "|E|", "Simple Randomized", "Simple Greedy", "Semi-greedy",
                "Simple local No. of iterations", "Simple local Average value",
                "GRASP No. of iterations", "GRASP Best value", "Known best solution or upper bound"});

        // Process 54 benchmark graphs (adjust file paths as needed)
//        String inputDir = "path/to/benchmark/graphs/"; // Replace with actual path
        for (int i = 1; i <= 54; i++) {
            String problemName = "g" + i;
            String filePath ="set1/g" + i + ".rud";
            try {
                // Read graph
                Graph graph = readGraph(filePath);
                Grasp grasp = new Grasp();
                Grasp.GraspResult result = grasp.calculateGrasp(graph, alpha, graspIterations, randomizedIterations);

                // Prepare CSV row
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
                csvRows.add(row);

                System.out.println("Processed " + problemName);
            } catch (IOException e) {
                System.err.println("Error processing " + problemName + ": " + e.getMessage());
            }
        }

        // Write CSV
        try (PrintWriter writer = new PrintWriter(new File(csvFile))) {
            for (String[] row : csvRows) {
                writer.println(String.join(",", row));
            }
            System.out.println("CSV file generated: " + csvFile);
        } catch (FileNotFoundException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

}