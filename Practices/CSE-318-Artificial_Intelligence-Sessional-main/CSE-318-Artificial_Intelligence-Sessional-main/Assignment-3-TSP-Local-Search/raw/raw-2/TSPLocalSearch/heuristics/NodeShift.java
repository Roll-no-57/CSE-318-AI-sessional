package heuristics;

import problem.TSP;
import problem.TSPSolution;
import util.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class NodeShift implements PerturbativeHeuristic{

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
                for(int hop=1; hop<n-1; hop++){
                    int j = (i+hop)%n;

                    int A_prev = cycle.get((i-1+n)%n);
                    int A = cycle.get(i);
                    int A_next = cycle.get((i+1)%n);
                    int B1 = cycle.get(j);
                    int B2 = cycle.get((j+1)%n);

                    double changeOfCost = changeOfCostAfterNodeShift(tsp, A_prev, A, A_next, B1, B2);
                    if(changeOfCost < 0 && Math.abs(changeOfCost) > Constants.FRACTION_THRESHOLD){
                        // left shift by 1 position for [i+1...j](circular)
                        int l = i,r = (i+1)%n;
                        while(l != j){
                            int newVal = cycle.get(r);
                            cycle.set(l,newVal);
                            l = (l+1)%n;
                            r = (r+1)%n;
                        }
                        cycle.set(j,A);
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

    double changeOfCostAfterNodeShift(TSP tsp, int A_prev, int A, int A_next, int B1, int B2){
        double decreased = 0, increased = 0;
        decreased = tsp.getDistance(A_prev,A)+tsp.getDistance(A,A_next)+tsp.getDistance(B1,B2);
        increased = tsp.getDistance(A_prev,A_next)+tsp.getDistance(B1,A)+tsp.getDistance(A,B2);
        double changeOfCost = increased - decreased;
        return changeOfCost;
    }
}
