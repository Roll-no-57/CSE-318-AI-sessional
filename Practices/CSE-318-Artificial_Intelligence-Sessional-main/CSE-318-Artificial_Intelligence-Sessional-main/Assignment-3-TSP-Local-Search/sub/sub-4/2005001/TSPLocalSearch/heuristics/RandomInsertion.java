package heuristics;

import problem.TSP;
import problem.TSPSolution;
import util.RandUtil;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class RandomInsertion implements ConstructiveHeuristic{
    private class Pair {
        int addAfterIndex;
        double gain;

        public Pair(int addAfterIndex, double gain) {
            this.addAfterIndex = addAfterIndex;
            this.gain = gain;
        }

        @Override
        public String toString() {
            return "( addAfterIndex = " + addAfterIndex + ", gain = " + gain + ")";
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
            ArrayList<Integer>candidateNewNodes = new ArrayList<>();
            for(int i=1; i<=n; i++){
                if(!visited[i]){
                    candidateNewNodes.add(i);
                }
            }

            int newNode = RandUtil.getRandomElement(candidateNewNodes);

            if(verbose){
                System.out.println("Randomly chosen new node = " + newNode);
            }

            ArrayList<Pair>candidates = new ArrayList<>();
            PriorityQueue<Pair> pq = new PriorityQueue<>((p1, p2) -> Double.compare(p1.gain, p2.gain));

            int L = cycle.size();
            for(int j=0; j<L; j++){
                int u = cycle.get(j);
                int v = cycle.get((j+1)%L);
                // so we consider adding randomlyChosenNewNode in between u and v
                double gain = tsp.getDistance(newNode,u) + tsp.getDistance(newNode,v) - tsp.getDistance(u,v);
                pq.add(new Pair(j, gain));
            }

            // taking at max firstK of the nearest nodes to curr
            for(int i=0; i<firstK; i++){
                if(!pq.isEmpty()){
                    candidates.add(pq.poll());
                } else{
                    break;
                }
            }

            Pair chosenPair = RandUtil.getRandomElement(candidates);

            if(verbose) {
                System.out.println("Candidates: ");
                for (Pair p : candidates) {
                    System.out.println(p);
                }
                System.out.println("\nRandomly chosen = " + chosenPair);
            }

            cycle.add(chosenPair.addAfterIndex+1, newNode);
            visited[newNode] = true;
        }

        TSPSolution solution = new TSPSolution(tsp, cycle);

        return solution;
    }
}
