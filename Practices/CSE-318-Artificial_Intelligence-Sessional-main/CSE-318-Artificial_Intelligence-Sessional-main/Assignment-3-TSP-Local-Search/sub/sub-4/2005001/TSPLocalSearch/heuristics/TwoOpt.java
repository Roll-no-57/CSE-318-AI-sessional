package heuristics;

import problem.TSP;
import problem.TSPSolution;
import util.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class TwoOpt implements PerturbativeHeuristic{

    @Override
    public TSPSolution perturbSolution(TSPSolution solution, boolean verbose) {
        TSP tsp = solution.tsp;
        ArrayList<Integer>cycle = new ArrayList<>();

        // keeping the original solution intact
        cycle.addAll(solution.cycle);

        int L = cycle.size();

        // this max_iters is actually not that necessary
        // just adding it to an external bound to the runtime
        int max_iters = 5;

        boolean reachedLocalOptima;

        for(int iter=0; iter<max_iters; iter++) {
            reachedLocalOptima = true;
            // we will need to reverse the segment [i+1:j] later
            // so we set the loop traversal as such
            for(int i=0; i<L-1; i++){
                for(int j=i+2; j<L; j++){
                    int a,b,c,d;

                    a = cycle.get(i);
                    b = cycle.get((i+1)%L);
                    c = cycle.get(j);
                    d = cycle.get((j+1)%L);

                    if(a!=c && a!=d && b!=c && b!=d){
                        double changeOfCost = tsp.getDistance(a,c) + tsp.getDistance(b,d) - tsp.getDistance(a,b) - tsp.getDistance(c,d);
                        // if swapping results in better solution
                        if(changeOfCost < 0 && Math.abs(changeOfCost) > Constants.FRACTION_THRESHOLD){
                            int k1 = i+1;
                            int k2 = j;
                            while(k1<k2){
                                Collections.swap(cycle,k1,k2);
                                k1++;
                                k2--;
                            }
                            reachedLocalOptima = false;
                            if(verbose){
                                System.out.println("Improved by "+(-changeOfCost));
                            }
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
}
