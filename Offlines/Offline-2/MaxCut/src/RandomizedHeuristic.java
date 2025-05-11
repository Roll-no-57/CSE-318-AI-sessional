import java.util.Random;

public class RandomizedHeuristic {

    public double calculateRandomizedHeuristic(Graph graph,int numIterations){
        double totalCutWeight = 0;
        Random random = new Random();

        for(int i=1;i<=numIterations;i++){
            boolean[] partitionX = new boolean[graph.numVertices+1];

            for(int j=1;j<=graph.numVertices;j++){
                partitionX[j] = random.nextBoolean();
            }

            totalCutWeight += graph.cutWeight(partitionX);
        }

        return  totalCutWeight / numIterations;

    }
}
