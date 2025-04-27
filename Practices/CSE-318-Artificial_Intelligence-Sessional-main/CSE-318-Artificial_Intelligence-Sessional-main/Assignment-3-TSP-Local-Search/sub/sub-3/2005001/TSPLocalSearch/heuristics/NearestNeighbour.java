package heuristics;

import problem.TSP;
import problem.TSPSolution;
import util.RandUtil;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class NearestNeighbour implements ConstructiveHeuristic{
    private class Pair {
        int node;
        double distance;

        public Pair(int first, double distance) {
            this.node = first;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "(node = " + node + ", distance = " + distance + ")";
        }
    }
    @Override
    public TSPSolution constructSolution(TSP tsp, int firstK, boolean verbose) {
        int n = tsp.n;

        ArrayList<Integer>cycle = new ArrayList<>();

        int initialNode = RandUtil.getRandomOneIndexedNode(n);

        if(verbose) {
            System.out.println("Randomly chosen initial node = " + initialNode);
        }

        boolean[] visited = new boolean[n+1];
        for(int i=1; i<=n; i++){
            visited[i] = false;
        }

        cycle.add(initialNode);
        visited[initialNode] = true;

        int curr = initialNode;

        while(cycle.size() < n){
            ArrayList<Pair>candidates = new ArrayList<>();
            PriorityQueue<Pair>pq = new PriorityQueue<>((p1, p2) -> Double.compare(p1.distance, p2.distance));
            for(int i=1; i<=n; i++){
                if(!visited[i]){
                    pq.add(new Pair(i, tsp.getDistance(curr,i)));
                }
            }

            // taking at max firstK of the nearest nodes to curr
            for(int i=0; i<firstK; i++){
                if(!pq.isEmpty()){
                    candidates.add(pq.poll());
                } else{
                    break;
                }
            }

            int chosenNextNode = RandUtil.getRandomElement(candidates).node;

            if(verbose) {
                System.out.println("Looking from " + curr);
                System.out.println("Candidates: ");
                for (Pair p : candidates) {
                    System.out.println(p);
                }
                System.out.println("\nRandomly chosen = " + chosenNextNode);
            }

            cycle.add(chosenNextNode);
            visited[chosenNextNode] = true;
            curr = chosenNextNode;
        }

        TSPSolution solution = new TSPSolution(tsp, cycle);

        return solution;

    }
}
