import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;



public class Graph {
    public int numEdges;
    public int numVertices;
    public List<Map<Integer,Integer>> adjList;
    public Edge maxEdge;
    public Edge minEdge;


    // The graph we declared as an adjacency list
    public Graph(int numVertices, int numEdges) {
        this.numVertices = numVertices;
        this.numEdges = numEdges;

        adjList = new ArrayList<>(numVertices+1);
        for(int i=0;i<numVertices+1;i++){
            adjList.add(new HashMap<>());
        }
    }

    // It is an undirected graph.
    public void addEdge(int from, int to, int weight){
        adjList.get(from).put(to,weight);
        adjList.get(to).put(from,weight);
    }

    public void findCriticalEdges() {
        Edge maxEdge = null;
        Edge minEdge = null;
        int maxWeight = Integer.MIN_VALUE;
        int minWeight = Integer.MAX_VALUE;

        for (int u = 1; u <= numVertices; u++) {
            for (Map.Entry<Integer, Integer> entry : adjList.get(u).entrySet()) {
                int v = entry.getKey();
                int weight = entry.getValue();

                // ensure that duplicate vertex is not considered (u< v)
                if (u < v && weight > maxWeight) {
                    maxWeight = weight;
                    maxEdge = new Edge(u, v, weight);
                }
                if (u < v && weight < minWeight) {
                    minWeight = weight;
                    minEdge = new Edge(u, v, weight);
                }
            }
        }
        this.maxEdge = maxEdge != null ? maxEdge : new Edge(0, 0, 0); // Default edge
        this.minEdge = minEdge != null ? minEdge : new Edge(0, 0, 0); // Default edge
    }


    public partitionCut getCutContribution(int v, Set<Integer> partitionX, Set<Integer> partitionY) {
        double sigmaX = 0, sigmaY = 0;
        for (int y : partitionY) {
            Integer weight = adjList.get(v).get(y);
            if (weight != null) {
                sigmaX += weight;
            }
        }
        for (int x : partitionX) {
            Integer weight = adjList.get(v).get(x);
            if (weight != null) {
                sigmaY += weight;
            }
        }
        return new partitionCut(sigmaX, sigmaY);
    }

    public double cutWeight(boolean[] partitionX) {
        double cutWeight = 0;
        for (int u = 1; u < partitionX.length; u++) {
            if (partitionX[u]) {
                for (int v : adjList.get(u).keySet()) {
                    if (!partitionX[v]) {
                        cutWeight += adjList.get(u).get(v);
                    }
                }
            }
        }
        return cutWeight;
    }

    public double totalCutValue(Set<Integer> partitionX,Set<Integer> partitionY){
        double FinalCutValue = 0;
        for (int u : partitionX) {
            for (int v : partitionY) {
                Integer weight = adjList.get(u).get(v);
                if (weight != null) {
                    FinalCutValue += weight;
                }
            }
        }
        return FinalCutValue;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "numEdges=" + numEdges +
                ", numVertices=" + numVertices +
                ", adjList=" + adjList +
                '}';
    }
}
