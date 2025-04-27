package heuristics;

import problem.TSP;
import problem.TSPSolution;
import util.RandUtil;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class CheapestInsertion implements ConstructiveHeuristic{
    class Triplet {
        int newNode;
        int addAfterIndex;
        double gain;

        public Triplet(int newNode, int addAfterIndex, double gain) {
            this.newNode = newNode;
            this.addAfterIndex = addAfterIndex;
            this.gain = gain;
        }

        @Override
        public String toString() {
            return "( newNode = " + newNode + ", addAfterIndex = " + addAfterIndex + ", gain = " + gain + ")";
        }
    }
    @Override
    public TSPSolution constructSolution(TSP tsp, int firstK, boolean verbose) {
        int n = tsp.n;

        ArrayList<Integer> cycle = new ArrayList<>();

        int firstInitialNode = RandUtil.getRandomOneIndexedNode(n);

        int minV = -1;
        double minD = Double.POSITIVE_INFINITY;

        for(int v=1; v<=n; v++){
            if(v != firstInitialNode){
                double d = tsp.getDistance(firstInitialNode, v);
                if(d < minD){
                    minD = d;
                    minV = v;
                }
            }
        }

        int secondInitialNode = minV;

        if(verbose){
            System.out.println("First two initial nodes = " + firstInitialNode + ", " + secondInitialNode);
        }

        boolean[] visited = new boolean[n+1];
        for(int i=1; i<=n; i++){
            visited[i] = false;
        }

        cycle.add(firstInitialNode);
        visited[firstInitialNode] = true;

        cycle.add(secondInitialNode);
        visited[secondInitialNode] = true;

        while(cycle.size() < n){
            ArrayList<Triplet>candidates = new ArrayList<>();
            PriorityQueue<Triplet> pq = new PriorityQueue<>((t1, t2) -> Double.compare(t1.gain, t2.gain));

            for(int newNode=1; newNode<=n; newNode++){
                if(!visited[newNode]){
                    int L = cycle.size();
                    for(int j=0; j<L; j++){
                        int u = cycle.get(j);
                        int v = cycle.get((j+1)%L);
                        // so we consider adding i in between u and v
                        double gain = tsp.getDistance(newNode,u) + tsp.getDistance(newNode,v) - tsp.getDistance(u,v);
                        pq.add(new Triplet(newNode, j, gain));
                    }
                }
            }

            // taking at max firstK of the cheapest insertions
            for(int i=0; i<firstK; i++){
                if(!pq.isEmpty()){
                    candidates.add(pq.poll());
                } else{
                    break;
                }
            }

            Triplet chosenTriplet = RandUtil.getRandomElement(candidates);

            if(verbose) {
                System.out.println("Candidates: ");
                for (Triplet t : candidates) {
                    System.out.println(t);
                }
                System.out.println("\nRandomly chosen = " + chosenTriplet);
            }

            cycle.add(chosenTriplet.addAfterIndex+1, chosenTriplet.newNode);
            visited[chosenTriplet.newNode] = true;
        }

        TSPSolution solution = new TSPSolution(tsp, cycle);

        return solution;
    }
}
