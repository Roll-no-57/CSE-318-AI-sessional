package heuristics;

import problem.TSP;
import problem.TSPSolution;

public interface PerturbativeHeuristic {
    public TSPSolution perturbSolution(TSPSolution solution, boolean verbose);
}
