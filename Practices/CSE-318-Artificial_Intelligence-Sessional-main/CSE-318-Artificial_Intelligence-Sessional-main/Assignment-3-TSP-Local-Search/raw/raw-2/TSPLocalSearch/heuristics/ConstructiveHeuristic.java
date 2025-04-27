package heuristics;

import problem.TSP;
import problem.TSPSolution;

public interface ConstructiveHeuristic {
    // here, firstK refers to how many of the smallest values we pick
    // for choosing from them randomly
    // so, if it is 1, that means, totally greedy
    // in some cases, like MST, this parameter will be ignored
    public TSPSolution constructSolution(TSP tsp, int firstK, boolean verbose);
}
