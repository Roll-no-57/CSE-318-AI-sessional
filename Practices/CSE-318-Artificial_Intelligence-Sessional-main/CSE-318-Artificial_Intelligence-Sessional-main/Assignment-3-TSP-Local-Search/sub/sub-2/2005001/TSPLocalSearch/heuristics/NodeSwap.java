package heuristics;

import problem.TSP;
import problem.TSPSolution;
import util.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class NodeSwap implements PerturbativeHeuristic{

    @Override
    public TSPSolution perturbSolution(TSPSolution solution, boolean verbose) {
        TSP tsp = solution.tsp;
        int n = tsp.n;
        ArrayList<Integer> cycle = new ArrayList<>();

        // keeping the original solution intact
        cycle.addAll(solution.cycle);

        int L = cycle.size();

        // this max_iters is actually not that necessary
        // just adding it to an external bound to the runtime
        int max_iters = 5;

        boolean reachedLocalOptima;

        for (int iter = 0; iter < max_iters; iter++) {
            reachedLocalOptima = true;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    int A_prev = cycle.get((i-1+n)%n);
                    int A = cycle.get(i);
                    int A_next = cycle.get((i+1)%n);
                    int B_prev = cycle.get((j-1+n)%n);
                    int B = cycle.get(j);
                    int B_next = cycle.get((j+1)%n);

                    double changeOfCost = changeOfCostAfterNodeSwap(tsp, A_prev, A, A_next, B_prev, B, B_next);

                    if(changeOfCost < 0 && Math.abs(changeOfCost) > Constants.FRACTION_THRESHOLD){
                        Collections.swap(cycle, i, j);
                        reachedLocalOptima = false;
                        if(verbose){
                            System.out.println("Improved by "+(-changeOfCost));
                        }
                    }
                }
            }
            // if no gains occured in this iteration
            if(reachedLocalOptima){
                if(verbose){
                    System.out.println("\nLocal Optima is reached. No more gain can be achieved by perturbing.");
                }
                break;
            }
        }

        TSPSolution perturbedSolution = new TSPSolution(tsp, cycle);

        return perturbedSolution;
    }

    double changeOfCostAfterNodeSwap(TSP tsp, int A_prev, int A, int A_next, int B_prev, int B, int B_next){
        double decreased = 0, increased = 0;
        if(B == A_next || B_prev == A_next){
            decreased = tsp.getDistance(A_prev, A) + tsp.getDistance(B, B_next);
            increased = tsp.getDistance(A_prev, B) + tsp.getDistance(A, B_next);
        } else if(A == B_next || A_prev == B_next){
            decreased = tsp.getDistance(B_prev, B) + tsp.getDistance(A, A_next);
            increased = tsp.getDistance(B_prev, A) + tsp.getDistance(B, A_next);
        } else{
            decreased = tsp.getDistance(A_prev, A) + tsp.getDistance(A, A_next) + tsp.getDistance(B_prev, B) + tsp.getDistance(B, B_next);
            increased = tsp.getDistance(A_prev, B) + tsp.getDistance(B, A_next) + tsp.getDistance(B_prev, A) + tsp.getDistance(A, B_next);
        }
        double changeOfCost = increased - decreased;
        return changeOfCost;
    }
}
