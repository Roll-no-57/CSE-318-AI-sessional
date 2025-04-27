package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import classifier.DecisionTree;
import util.Config.*;

public class Runner {
    private final Dataset originalDataset;
    private static final int NUM_RUNS = 20;
    private static final String CSV_FILE = "results/decision_tree_results.csv";
    private static final String DETAILED_CSV_FILE = "results/decision_tree_results_details.csv";

    public Runner(Dataset dataset) {
        this.originalDataset = dataset;
    }

    public void runAllTrainPcts(int min_train_pct, int max_train_pct) {
        try (FileWriter csvWriter = new FileWriter(CSV_FILE)) {
            csvWriter.append("trainPct,selectionStrategy,evaluationMetric,accuracyPct,constructionTime,nodeCount,treeDepth," +
                    "buyingDepth,maintDepth,doorsDepth,personsDepth,lugBootDepth,safetyDepth\n");

            for (int trainPct = min_train_pct; trainPct <= max_train_pct; trainPct++) {
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

    public void printResults() {
        String csvFile = CSV_FILE;

        Map<String, Double> infoGainSums = new HashMap<>();
        Map<String, Double> giniSums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        infoGainSums.put("BEST", 0.0);
        infoGainSums.put("TOP_THREE", 0.0);
        giniSums.put("BEST", 0.0);
        giniSums.put("TOP_THREE", 0.0);
        counts.put("BEST", 0);
        counts.put("TOP_THREE", 0);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                double trainPct = Double.parseDouble(values[0]);
                String strategy = values[1];
                String metric = values[2];
                double accuracy = Double.parseDouble(values[3]);

                // process if trainPct is 80
                if (trainPct == 80) {
                    if (metric.equals("INFORMATION_GAIN")) {
                        infoGainSums.put(strategy, infoGainSums.get(strategy) + accuracy);
                        counts.put(strategy, counts.get(strategy) + 1);
                    } else if (metric.equals("GINI_IMPURITY")) {
                        giniSums.put(strategy, giniSums.get(strategy) + accuracy);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        double bestInfoGainAvg = counts.get("BEST") > 0 ?
                infoGainSums.get("BEST") / counts.get("BEST") : 0;
        double bestGiniAvg = counts.get("BEST") > 0 ?
                giniSums.get("BEST") / counts.get("BEST") : 0;
        double topThreeInfoGainAvg = counts.get("TOP_THREE") > 0 ?
                infoGainSums.get("TOP_THREE") / counts.get("TOP_THREE") : 0;
        double topThreeGiniAvg = counts.get("TOP_THREE") > 0 ?
                giniSums.get("TOP_THREE") / counts.get("TOP_THREE") : 0;

        System.out.println("+-----------------------------------------+--------------------------------------+");
        System.out.println("|                                         | Average accuracy over 20 runs        |");
        System.out.println("| Attribute selection strategy            +-----------------+--------------------+");
        System.out.println("|                                         | Information gain| Gini impurity      |");
        System.out.println("+-----------------------------------------+-----------------+--------------------+");
        System.out.printf("| Always select the best attribute        | %.2f           | %.2f              |%n",
                bestInfoGainAvg, bestGiniAvg);
        System.out.println("+-----------------------------------------+-----------------+--------------------+");
        System.out.printf("| Select one randomly from top three      | %.2f           | %.2f              |%n",
                topThreeInfoGainAvg, topThreeGiniAvg);
        System.out.println("+-----------------------------------------+-----------------+--------------------+");
    }
}
