import classifier.DecisionTree;
import util.Attribute;
import util.Config.*;
import util.Dataset;
import util.Runner;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        ArrayList<Attribute> attributes = new ArrayList<>();

        attributes.add(new Attribute("buying", new ArrayList<>(Arrays.asList("vhigh", "high", "med", "low"))));
        attributes.add(new Attribute("maint", new ArrayList<>(Arrays.asList("vhigh", "high", "med", "low"))));
        attributes.add(new Attribute("doors", new ArrayList<>(Arrays.asList("2", "3", "4", "5more"))));
        attributes.add(new Attribute("persons", new ArrayList<>(Arrays.asList("2", "4", "more"))));
        attributes.add(new Attribute("lug_boot", new ArrayList<>(Arrays.asList("small", "med", "big"))));
        attributes.add(new Attribute("safety", new ArrayList<>(Arrays.asList("low", "med", "high"))));

        Dataset carDataset = new Dataset("car-dataset", attributes, new ArrayList<>(Arrays.asList("unacc", "acc", "good", "vgood")), "./car-evaluation-dataset/car.data");

        Runner runner = new Runner(carDataset);

        runner.runAllTrainPcts(80,80);
        runner.printResults();  // only for 80

        // for detiled csv run this
        //runner.runSingleTrainPctDetailed(80);

        // test(carDataset);
    }

    public static void test(Dataset carDataset) throws Exception {
        AttributeSelectionStrategy[] strategies = new AttributeSelectionStrategy[]{AttributeSelectionStrategy.BEST, AttributeSelectionStrategy.TOP_THREE};
        EvaluationMetric[] metrics = new EvaluationMetric[]{EvaluationMetric.INFORMATION_GAIN, EvaluationMetric.GINI_IMPURITY};

        System.out.println(carDataset);

        Dataset[] splitDatasets = carDataset.trainTestSplit(10);

        Dataset trainDataset = splitDatasets[0];
        Dataset testDataset = splitDatasets[1];

        long startTime = System.nanoTime();
        DecisionTree decisionTree = new DecisionTree(trainDataset, strategies[0], metrics[1]);
        long endTime = System.nanoTime();

        long elapsedTime = endTime - startTime;
        System.out.println("Construction time: " + (elapsedTime/1e6) + " milliseconds");

        System.out.println(decisionTree.calculateAccuracy(testDataset));
        System.out.println("Tree Nodes = " + decisionTree.getTreeSize());
        System.out.println("Tree Depth = " + decisionTree.getTreeDepth());

        // comes in the same order of attributes, as added above
        // these depths are logged in the csv in the columns in same order
        ArrayList<Double> avgDepths = decisionTree.calculateAttributeAverageDepths();
    }


}