package classifier;

import util.Attribute;
import util.Config.*;
import util.DataPoint;
import util.Dataset;

import java.util.*;



public class DecisionTree {
    AttributeSelectionStrategy attributeSelectionStrategy;
    EvaluationMetric evaluationMetric;

    public ArrayList<Attribute> attributes;

    public Node root;

    public DecisionTree(Dataset trainDataset, AttributeSelectionStrategy attributeSelectionStrategy, EvaluationMetric evaluationMetric) throws Exception {
        if(trainDataset.datapoints.isEmpty()){
            throw new Exception("Sorry! Empty dataset cannot be processed.");
        }
        this.attributes = trainDataset.attributes;
        this.attributeSelectionStrategy = attributeSelectionStrategy;
        this.evaluationMetric = evaluationMetric;
        this.root = constructDecisionTree(trainDataset.datapoints, trainDataset.attributes, new ArrayList<>());
    }

    class Pair<K,V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    public Node constructDecisionTree(ArrayList<DataPoint> datapoints, ArrayList<Attribute>remainingAttributes, ArrayList<DataPoint> parentDatapoints) throws Exception {
        if(datapoints.isEmpty()){
            return new Node(true, getPluralityLabel(parentDatapoints), null);
        } else if (isOfSameClass(datapoints)) {
            return new Node(true, datapoints.get(0).label, null);
        } else if (remainingAttributes.isEmpty()) {
            return new Node(true, getPluralityLabel(datapoints), null);
        } else {
            double metricBeforeSplit = getMetric(datapoints);

            PriorityQueue<Pair<Attribute, Double>> topAttributes =
                    new PriorityQueue<>((p1, p2) -> Double.compare(p2.getValue(), p1.getValue()));

            for(Attribute a: remainingAttributes) {
                double metricAfterSplit = getMetricAfterSplit(datapoints, a);
                double gain = metricBeforeSplit - metricAfterSplit;
                topAttributes.add(new Pair<>(a, gain));
            }

            Attribute optimalAttribute;

            if(attributeSelectionStrategy == AttributeSelectionStrategy.TOP_THREE){
                int numTop = Math.min(3, topAttributes.size());
                ArrayList<Attribute> candidates = new ArrayList<>();
                for(int i = 0; i < numTop; i++) {
                    candidates.add(topAttributes.poll().getKey());
                }
                optimalAttribute = candidates.get(new Random().nextInt(candidates.size()));
            } else if (attributeSelectionStrategy == AttributeSelectionStrategy.BEST){
                optimalAttribute = topAttributes.poll().getKey();
            } else {
                throw new Exception("Unknown selection strategy");
            }

            ArrayList<Attribute> currentRemainingAttributes = new ArrayList<>(remainingAttributes);
            currentRemainingAttributes.remove(optimalAttribute);
            HashMap<String, ArrayList<DataPoint>> splitMap = getSplitMap(datapoints, optimalAttribute);
            Node node = new Node(false, null, optimalAttribute);
            for(String value: optimalAttribute.possibleValues){
                ArrayList<DataPoint> childDataPoints = splitMap.getOrDefault(value, new ArrayList<>());
                node.addChild(value, constructDecisionTree(childDataPoints, currentRemainingAttributes, datapoints));
            }
            return node;
        }
    }
    
    public Boolean isOfSameClass(ArrayList<DataPoint> datapoints){
        if(datapoints.isEmpty()) return true;
        String firstLabel = datapoints.get(0).label;
        for(DataPoint d: datapoints){
            if(!d.label.equals(firstLabel)){
                return false;
            }
        }
        return true;
    }

    public String getPluralityLabel(ArrayList<DataPoint> datapoints){
        HashMap<String, Integer> labelCounts = getLabelCountMap(datapoints);

        int maxCount = 0;
        ArrayList<String> maxLabels = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : labelCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxLabels.clear();
                maxLabels.add(entry.getKey());
            } else if (entry.getValue() == maxCount) {
                maxLabels.add(entry.getKey());
            }
        }

        // in case of tie
        // break tie randomly
        return maxLabels.get(new Random().nextInt(maxLabels.size()));
    }

    public double getMetricAfterSplit(ArrayList<DataPoint> datapoints, Attribute a) throws Exception {
        double metric = 0.0;

        int total = datapoints.size();

        HashMap<String, ArrayList<DataPoint>> splitMap = getSplitMap(datapoints, a);

        for (Map.Entry<String, ArrayList<DataPoint>> entry : splitMap.entrySet()) {
            ArrayList<DataPoint> currentSplitDatapoints = entry.getValue();
            double weight = currentSplitDatapoints.size()*1.0 / total;
            metric += weight* getMetric(entry.getValue());
        }

        return metric;
    }

    public double getMetric(ArrayList<DataPoint> datapoints) throws Exception {
        double metric;

        int total = datapoints.size();

        HashMap<String, Integer> labelCounts = getLabelCountMap(datapoints);

        if(evaluationMetric == EvaluationMetric.INFORMATION_GAIN){
            double entropy = 0.0;
            for (Map.Entry<String, Integer> entry : labelCounts.entrySet()) {
                double p = entry.getValue()*1.0 / total;
                if(p != 0){
                    entropy += (-(p*log2(p)));
                }
            }
            metric = entropy;
        } else if (evaluationMetric == EvaluationMetric.GINI_IMPURITY){
            double sum = 1.0;
            for (Map.Entry<String, Integer> entry : labelCounts.entrySet()) {
                double p = entry.getValue()*1.0 / total;
                if(p != 0){
                    sum += p*p;
                }
            }
            metric = 1-sum;
        } else{
            throw new Exception("Unknown Metric");
        }

        return metric;
    }

    private HashMap<String, Integer> getLabelCountMap(ArrayList<DataPoint> datapoints){
        HashMap<String, Integer> labelCounts = new HashMap<>();
        for (DataPoint d : datapoints) {
            labelCounts.put(d.label, labelCounts.getOrDefault(d.label, 0) + 1);
        }
        return labelCounts;
    }

    private HashMap<String, ArrayList<DataPoint>> getSplitMap(ArrayList<DataPoint> datapoints, Attribute a){
        HashMap<String, ArrayList<DataPoint>> splitMap = new HashMap<>();
        for (DataPoint d : datapoints) {
            String value = d.attributeValues.get(a);
            if(!splitMap.containsKey(value)){
                splitMap.put(value, new ArrayList<>());
            }
            splitMap.get(value).add(d);
        }
        return splitMap;
    }

    private double log2(double v){
        return (Math.log(v)/(Math.log(2)));
    }

    public double calculateAccuracy(Dataset testDataset) throws Exception {
        int total = testDataset.datapoints.size();
        if(total == 0){
            throw new Exception("Empty test dataset!");
        }
        int correct = 0;
        for(DataPoint d: testDataset.datapoints){
            String outputLabel = classify(d.attributeValues);
            if(outputLabel.equals(d.label)){
                correct++;
            }
        }
        return (correct*100.0)/total;
    }

    public ArrayList<String> getOutputLabels(Dataset testDataset) throws Exception {
        ArrayList<String>outputLabels = new ArrayList<>();
        for(DataPoint d: testDataset.datapoints){
            String outputLabel = classify(d.attributeValues);
            outputLabels.add(outputLabel);
        }
        return outputLabels;
    }

    public String classify(HashMap<Attribute, String> attributeValues) throws Exception {
        Node currentNode = this.root;
        while(!currentNode.isLeaf){
            Attribute a = currentNode.attributeToTest;
            if(!attributeValues.containsKey(a)){
                throw new Exception("Attribute " + a.name + " not found in the given data");
            }
            String v = attributeValues.get(a);
            if(!currentNode.childrenMap.containsKey(v)){
                throw new Exception("No branch found for the value " + v + " of " + a.name + " in the decision tree.");
            }
            currentNode = currentNode.childrenMap.get(v);
        }
        return currentNode.outputLabel;
    }

    public int getTreeDepth(){
        return root.getSubtreeDepth();
    }

    public int getTreeSize(){
        return root.getSubtreeSize();
    }

    public ArrayList<Double> calculateAttributeAverageDepths() {
        HashMap<Attribute, ArrayList<Integer>> attributeDepths = new HashMap<>();

        for (Attribute a : this.attributes) {
            attributeDepths.put(a, new ArrayList<>());
        }

        collectAttributeDepths(root, 0, attributeDepths);

        ArrayList<Double> averageDepths = new ArrayList<>();
        for (Attribute attr : this.attributes) {
            ArrayList<Integer> depths = attributeDepths.get(attr);
            if (depths.isEmpty()) {
                averageDepths.add(-1.0);
            } else {
                double sum = 0;
                for (int depth : depths) {
                    sum += depth;
                }
                averageDepths.add(sum / depths.size());
            }
        }

        return averageDepths;
    }

    private void collectAttributeDepths(Node node, int currentDepth,
                                        HashMap<Attribute, ArrayList<Integer>> attributeDepths) {
        if (node == null || node.isLeaf) {
            return;
        }

        attributeDepths.get(node.attributeToTest).add(currentDepth);

        for (Node child : node.childrenMap.values()) {
            collectAttributeDepths(child, currentDepth + 1, attributeDepths);
        }
    }


}
