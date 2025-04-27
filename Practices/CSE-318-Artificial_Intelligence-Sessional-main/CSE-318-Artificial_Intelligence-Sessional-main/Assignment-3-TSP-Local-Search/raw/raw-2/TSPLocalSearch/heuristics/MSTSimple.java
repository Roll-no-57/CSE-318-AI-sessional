package heuristics;

import problem.TSP;
import problem.TSPSolution;
import util.Edge;
import util.GraphUtil;

import java.util.ArrayList;

public class MSTSimple implements ConstructiveHeuristic{
    @Override
    public TSPSolution constructSolution(TSP tsp, int firstK, boolean verbose) {
        ArrayList<Edge> mstEdges = GraphUtil.findMST(tsp.n, tsp.dist);
        ArrayList<Integer> eulerTour = GraphUtil.findEulerTour(tsp.n, mstEdges);
        TSPSolution solution = new TSPSolution(tsp, eulerTour);
        return solution;
    }
}
