package util;

import java.util.*;

public class GraphUtil {
    // dsu allows us to
    // connect two sets or check connectivity very efficiently
    private static class DisjointSetUnion {
        private int[] parent;
        private int[] rank;

        public DisjointSetUnion(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 1;
            }
        }

        // here we are doing path compression
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        // union by rank
        // here rank actually means
        // the depth of that tree
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }

        public boolean isConnected(int x, int y) {
            return find(x) == find(y);
        }
    }

    // gives the edge list of the MST
    // note that, it gives the edges as undirected
    // that means, it will, say, just return (1,2) instead of (1,2),(2,1) both
    // but this will mean, undirected edges
    public static ArrayList<Edge> findMST(int n, double[][] dist){
        ArrayList<Edge>edges = new ArrayList<>();
        ArrayList<Edge>mstEdges = new ArrayList<>();

        // make we do not add unnessary edges or self-loops
        // though they would already be avoided actually
        for(int u=1; u<=n; u++){
            for(int v=u+1; v<=n; v++){
                edges.add(new Edge(u,v,dist[u][v]));
            }
        }
        Collections.sort(edges, Comparator.comparingDouble(e -> e.w));

        DisjointSetUnion dsu = new DisjointSetUnion(n+1);

        for(Edge e: edges){
            if(!dsu.isConnected(e.u, e.v)){
                dsu.union(e.u, e.v);
                mstEdges.add(e);
            } if(mstEdges.size() == n-1){
                break;
            }
        }

        return mstEdges;
    }

    public static ArrayList<Integer> findEulerTour(int n, ArrayList<Edge> edges) {
        Map<Integer, ArrayList<Integer>> adjList = new HashMap<>();
        for(int u=1; u<=n; u++){
            adjList.put(u, new ArrayList<>());
        }
        for (Edge edge : edges) {
            adjList.get(edge.u).add(edge.v);
            // since undirected edge
            adjList.get(edge.v).add(edge.u);
        }

        ArrayList<Integer> eulerTour = new ArrayList<>();

        boolean[] visited = new boolean[n+1];
        boolean[] addedToTour = new boolean[n+1];
        for(int u=1; u<=n; u++){
            visited[u] = false;
            addedToTour[u] = false;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(edges.size());

        int startingNode = edges.get(randomIndex).u;

        dfs(startingNode, adjList, eulerTour, visited, addedToTour);

        return eulerTour;
    }

    public static void dfs(int u, Map<Integer, ArrayList<Integer>> adjList, ArrayList<Integer> eulerTour, boolean[] visited, boolean[] addedToTour){
        visited[u] = true;
        if(!addedToTour[u]){
            eulerTour.add(u);
            addedToTour[u] = true;
        }
        for(int v: adjList.get(u)){
            if(!visited[v]){
                dfs(v, adjList, eulerTour, visited, addedToTour);
            }
        }
    }
}

