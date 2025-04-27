package problem;

public class TSP {
    public String name;
    public int n;

    // 1-indexed array
    public double[][] dist;

    public TSP(String name, int n){
        this.name = name;
        this.n = n;
        this.dist = new double[n+1][n+1];
        for(int i=1; i<=n; i++){
            for(int j=1; j<=n; j++){
                if(i == j){
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }

    public double getDistance(int i, int j){
        return dist[i][j];
    }

    public void setDistance(int i, int j, double d){
        dist[i][j] = d;
    }
}
