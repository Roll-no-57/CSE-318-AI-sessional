package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import classifier.DecisionTree;
import util.Config.*;

public class Runner {
    private final Dataset originalDataset;
    private static final int NUM_RUNS = 20;
    private static final int MIN_TRAIN_PCT = 1;
    private static final int MAX_TRAIN_PCT = 99;
    private static final String CSV_FILE = "results/decision_tree_results.csv";
    private static final String DETAILED_CSV_FILE = "results/decision_tree_results_details.csv";

    public Runner(Dataset dataset) {
        this.originalDataset = dataset;
    }

    public void runAllTrainPcts() {
        try (FileWriter csvWriter = new FileWriter(CSV_FILE)) {
            csvWriter.append("trainPct,selectionStrategy,evaluationMetric,accuracyPct,constructionTime,nodeCount,treeDepth," +
                    "buyingDepth,maintDepth,doorsDepth,personsDepth,lugBootDepth,safetyDepth\n");

            for (int trainPct = MIN_TRAIN_PCT; trainPct <= MAX_TRAIN_PCT; trainPct++) {
                for (AttributeSelectionStrategy strategy : new AttributeSelectionStrategy[]{
                        AttributeSelectionStrategy.BEST, AttributeSelectionStrategy.TOP_THREE}) {
                    for (EvaluationMetric metric : new EvaluationMetric[]{
                            EvaluationMetric.INFORMATION_GAIN, EvaluationMetric.GINI_IMPURITY}) {

                        for (int run = 0; run < NUM_RUNS; run++) {
                            try {
                                Dataset[] splitDatasets = originalDataset.trainTestSplit(trainPct);
                                Dataset trainDataset = splitDatasets[0];
                                Dataset testDataset = splitDatasets[1];

                                long startTime = System.nanoTime();
                                DecisionTree decisionTree = new DecisionTree(trainDataset, strategy, metric);
                                double constructionTime = (System.nanoTime() - startTime) / 1e6;

                                double accuracy = decisionTree.calculateAccuracy(testDataset);
                                int nodeCount = decisionTree.getTreeSize();
                                int treeDepth = decisionTree.getTreeDepth();
                                ArrayList<Double> attributeDepths = decisionTree.calculateAttributeAverageDepths();

                                StringBuilder row = new StringBuilder();
                                row.append(trainPct).append(",")
                                        .append(strategy).append(",")
                                        .append(metric).append(",")
                                        .append(String.format("%.4f", accuracy)).append(",")
                                        .append(constructionTime).append(",")
                                        .append(nodeCount).append(",")
                                        .append(treeDepth);

                                for (Double depth : attributeDepths) {
                                    row.append(",").append(String.format("%.2f", depth));
                                }

                                row.append("\n");
                                csvWriter.append(row.toString());

                                if (run % 5 == 0) {
                                    csvWriter.flush();
                                }

                            } catch (Exception e) {
                                System.err.printf("Error in run: TrainPct=%d, Strategy=%s, Metric=%s, Run=%d\n",
                                        trainPct, strategy, metric, run);
                                e.printStackTrace();
                            }
                        }
                    }
                }
                System.out.printf("Completed: TrainPct = %d\n", trainPct);
            }

            csvWriter.flush();
            System.out.println("Runs complete! Results saved to " + CSV_FILE);

        } catch (IOException e) {
            System.err.println("Error writing to CSV file:");
            e.printStackTrace();
        }
    }

    public void runSingleTrainPctDetailed(int trainPct) {
        try (FileWriter csvWriter = new FileWriter(DETAILED_CSV_FILE)) {
            StringBuilder header = new StringBuilder();
            header.append("trainPct,selectionStrategy,evaluationMetric,run");
            for (Attribute a : originalDataset.attributes) {
                header.append(",").append(a.name);
            }
            header.append(",actualLabel,predictedLabel\n");
            csvWriter.append(header.toString());

            for (AttributeSelectionStrategy strategy : new AttributeSelectionStrategy[]{
                    AttributeSelectionStrategy.BEST, AttributeSelectionStrategy.TOP_THREE}) {
                for (EvaluationMetric metric : new EvaluationMetric[]{
                        EvaluationMetric.INFORMATION_GAIN, EvaluationMetric.GINI_IMPURITY}) {

                    for (int run = 0; run < NUM_RUNS; run++) {
                        try {
                            Dataset[] splitDatasets = originalDataset.trainTestSplit(trainPct);
                            Dataset trainDataset = splitDatasets[0];
                            Dataset testDataset = splitDatasets[1];

                            DecisionTree tree = new DecisionTree(trainDataset, strategy, metric);
                            ArrayList<String> predictions = tree.getOutputLabels(testDataset);

                            for (int i = 0; i < testDataset.datapoints.size(); i++) {
                                DataPoint d = testDataset.datapoints.get(i);
                                StringBuilder row = new StringBuilder();

                                row.append(trainPct).append(",")
                                        .append(strategy).append(",")
                                        .append(metric).append(",")
                                        .append(run + 1);

                                for (Attribute a : originalDataset.attributes) {
                                    row.append(",").append(d.attributeValues.get(a));
                                }

                                row.append(",").append(d.label)
                                        .append(",").append(predictions.get(i))
                                        .append("\n");

                                csvWriter.append(row.toString());
                            }

                            System.out.printf("Completed: Strategy=%s, Metric=%s, Run=%d/20\n",
                                    strategy, metric, run + 1);
                            if (run % 5 == 0) {
                                csvWriter.flush();
                            }

                        } catch (Exception e) {
                            System.err.printf("Error in run: Strategy=%s, Metric=%s, Run=%d\n",
                                    strategy, metric, run);
                            e.printStackTrace();
                        }
                    }
                }
            }

            csvWriter.flush();
            System.out.println("Detailed classification complete! Results saved to " + DETAILED_CSV_FILE);

        } catch (IOException e) {
            System.err.println("Error writing to CSV file:");
            e.printStackTrace();
        }
    }
}
