package problem;

import java.util.ArrayList;
import java.util.Collections;

public class TSPSolution {
    public TSP tsp;

    // note that, it is expected that, this list contains exactly n vertices
    // the first vertex should not be repeated
    public ArrayList<Integer>cycle;  // list of vertices in the cycle
    public double cost;
    public boolean isValid;

    public TSPSolution(TSP tsp, ArrayList<Integer> cycle) {
        this.tsp = tsp;
        this.cycle = cycle;
        this.cost = 0;
        int L = cycle.size();
        for(int i=0; i<L; i++){
            int u = cycle.get(i);
            int v = cycle.get((i+1)%L);
            this.cost += tsp.getDistance(u,v);
        }
        isValid = true;
        if(L != tsp.n){
            isValid = false;
        } else{
            ArrayList<Integer> temp = new ArrayList<>();
            // keeping the original solution intact
            temp.addAll(cycle);
            Collections.sort(temp);
            for(int i=0; i<L; i++){
                if(temp.get(i) != (i+1)){
                    isValid = false;
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("Generated Solution to "+tsp.name+":\n\n");
        sb.append("Number of nodes = "+tsp.n+"\n");
        sb.append("Nodes = ");
        for(int u: cycle){
            sb.append(u + " - ");
        }
        sb.append("\b\b\b\n");
        sb.append(String.format("Total cost = %.4f", cost));
        sb.append("\n");
        return sb.toString();
    }
}
