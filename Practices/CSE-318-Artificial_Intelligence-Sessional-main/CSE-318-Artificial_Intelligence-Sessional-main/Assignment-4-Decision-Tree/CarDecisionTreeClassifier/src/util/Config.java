package util;

public class Config {
    public static enum AttributeSelectionStrategy {
        BEST,
        TOP_THREE
    }

    public static enum EvaluationMetric {
        INFORMATION_GAIN,
        GINI_IMPURITY
    }
}
