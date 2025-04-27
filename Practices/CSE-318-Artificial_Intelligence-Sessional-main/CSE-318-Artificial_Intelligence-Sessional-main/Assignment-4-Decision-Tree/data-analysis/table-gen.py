import pandas as pd
import numpy as np

def generate_latex_table():
    csv_filepath = "./results/decision_tree_results.csv"

    # Read the CSV file
    df = pd.read_csv(csv_filepath)
    
    # Filter for trainPct = 80
    df = df[df['trainPct'] == 80]
    
    # Calculate averages for each strategy and metric
    results = {}
    for strategy in ['BEST', 'TOP_THREE']:
        strategy_df = df[df['selectionStrategy'] == strategy]
        
        # Calculate averages for both metrics
        info_gain_acc = strategy_df[
            strategy_df['evaluationMetric'] == 'INFORMATION_GAIN'
        ]['accuracyPct'].mean()
        
        gini_acc = strategy_df[
            strategy_df['evaluationMetric'] == 'GINI_IMPURITY'
        ]['accuracyPct'].mean()
        
        results[strategy] = {
            'Information gain': info_gain_acc,
            'Gini impurity': gini_acc
        }
    
    # Generate LaTeX table using f-string
    latex = f"""\\begin{{table}}[h]
\\begin{{tabular}}{{|p{{5cm}}|c|c|}}
\\hline
& \\multicolumn{{2}}{{c|}}{{Average accuracy over 20 runs}} \\\\
\\hline
Attribute selection strategy & Information gain & Gini impurity \\\\
\\hline
Always select the best attribute & {results['BEST']['Information gain']:.2f} & {results['BEST']['Gini impurity']:.2f} \\\\
\\hline
Select one randomly from the top three attributes & {results['TOP_THREE']['Information gain']:.2f} & {results['TOP_THREE']['Gini impurity']:.2f} \\\\
\\hline
\\end{{tabular}}
\\end{{table}}"""
    
    return latex

# Example usage
if __name__ == "__main__":
    latex_table = generate_latex_table()
    print(latex_table)