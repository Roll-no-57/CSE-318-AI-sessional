import heuristics.*;
import problem.TSP;
import problem.TSPSolution;
import util.FileParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        runAllTSP();
        //runSingleTSP();
    }

    public static void runAllTSP() throws IOException {
        ConstructiveHeuristic[] cons = {new NearestNeighbour(), new NearestNeighbour(), new CheapestInsertion(), new CheapestInsertion(), new RandomInsertion(), new MSTSimple()};
        int[] firstKvalues = {1, 3, 1, 3, 1, 1};
        PerturbativeHeuristic[] pers = {null, new TwoOpt(), new NodeSwap(), new NodeShift()};

        File benchmarksDir = new File("./benchmarks");

        File[] tspFiles = benchmarksDir.listFiles((dir, name) -> name.endsWith(".tsp"));

        int num_iter = 10;

        if (tspFiles != null) {
            try (PrintWriter csvWriter = new PrintWriter(new FileWriter("results.csv"))) {
                csvWriter.println("problem_name,constructive_heuristic,perturbative_heuristic,best_cost,average_cost,worst_cost");
                int counter = 0;
                for (File tspFile : tspFiles) {
                    counter += 1;
//                    if (counter > 1) {
//                        break;
//                    }
                    TSP tsp = FileParser.parseTSPFile(tspFile.getPath());
                    for (int i=0; i<cons.length; i++) {
                        for (PerturbativeHeuristic per : pers) {
                            ConstructiveHeuristic con = cons[i];
                            int firstK = firstKvalues[i];
                            String constructiveHeuristicName = con.getClass().getSimpleName();
                            if(firstK > 1){
                                constructiveHeuristicName = "SemiGreedy"+constructiveHeuristicName;
                            }
                            String perturbativeHeuristicName = per != null ? per.getClass().getSimpleName(): "---";
                            double best = Double.POSITIVE_INFINITY, sum = 0, worst = Double.NEGATIVE_INFINITY;
                            for (int iter = 0; iter < num_iter; iter++) {
                                TSPSolution sol = con.constructSolution(tsp, firstK, false);
                                if(per != null){
                                    sol = per.perturbSolution(sol, false);
                                }
                                if(!sol.isValid){
                                    System.out.println("\n\nUnexpected ERROR\n\n");
                                    return;
                                }
                                System.out.println("--------------------------------------------------");
                                if(per != null){
                                    System.out.println("Method = "+constructiveHeuristicName+"+"+perturbativeHeuristicName);
                                } else{
                                    System.out.println("Method = "+constructiveHeuristicName);
                                }
                                System.out.println("--------------------------------------------------");
                                System.out.println(sol);
                                double cost = sol.cost;
                                sum += cost;
                                best = Math.min(best, cost);
                                worst = Math.max(worst, cost);
                            }
                            double avg = sum / num_iter;
                            csvWriter.printf("%s,%s,%s,%.2f,%.2f,%.2f%n",
                                    tspFile.getName(),
                                    constructiveHeuristicName,
                                    perturbativeHeuristicName,
                                    best,
                                    avg,
                                    worst);
                        }
                    }

                }
            }
        } else {
            System.out.println("No .tsp files found in the directory.");
        }
    }

    public static void runSingleTSP() throws IOException {
        TSP tsp = FileParser.parseTSPFile("./benchmarks/a280.tsp");
        System.out.println(tsp.n);
        ConstructiveHeuristic con;

        con = new NearestNeighbour();
        // con = new CheapestInsertion();
        // con = new RandomInsertion();
//        con = new MSTSimple();

        PerturbativeHeuristic per;
//        per = new TwoOpt();
//        per = new NodeSwap();
        per = new NodeShift();

        TSPSolution sol = con.constructSolution(tsp, 1, false);
        sol = per.perturbSolution(sol, false);

        System.out.println(sol);
    }
}
