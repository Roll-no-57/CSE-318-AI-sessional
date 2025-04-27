import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

def analyze_perturbative_improvements(filepath):
    # Read the CSV data
    df = pd.read_csv(filepath)
    
    # Create a dictionary to store baseline costs (without perturbation)
    baselines = df[df['perturbative_h'] == '---'].set_index(
        ['problem_name', 'constructive_h']
    )['average_cost'].to_dict()
    
    # Filter out the baseline rows
    df_perturb = df[df['perturbative_h'] != '---'].copy()
    
    # Calculate improvement percentages
    def calculate_improvement(row):
        baseline = baselines[(row['problem_name'], row['constructive_h'])]
        return ((baseline - row['average_cost']) / baseline) * 100
    
    df_perturb['improvement_percentage'] = df_perturb.apply(calculate_improvement, axis=1)
    
    # Create the box plot
    plt.figure(figsize=(12, 6))
    sns.boxplot(x='perturbative_h', y='improvement_percentage', data=df_perturb)
    
    # Customize the plot
    plt.title('Improvement Percentages by Perturbative Algorithm', pad=20)
    plt.xlabel('Perturbative Algorithm')
    plt.ylabel('Improvement Percentage')
    
    # Add a grid for better readability
    plt.grid(True, axis='y', linestyle='--', alpha=0.7)
    
    # Calculate and display summary statistics
    summary_stats = df_perturb.groupby('perturbative_h')['improvement_percentage'].agg([
        ('median', 'median'),
        ('mean', 'mean'),
        ('std', 'std'),
        ('min', 'min'),
        ('max', 'max')
    ]).round(2)
    
    print("\nImprovement Statistics by Perturbative Algorithm:")
    print("\nMedian Improvements:")
    for algo in summary_stats.index:
        print(f"{algo}: {summary_stats.loc[algo, 'median']:.1f}%")
    
    print("\nDetailed Statistics:")
    print(summary_stats)
    
    return df_perturb, summary_stats

# Run the analysis
df_results, stats = analyze_perturbative_improvements('./results.csv')

# Show top 5 best improvements
print("\nTop 5 Best Improvements:")
top_improvements = df_results.nlargest(5, 'improvement_percentage')[
    ['problem_name', 'constructive_h', 'perturbative_h', 'improvement_percentage']
].round(2)
print(top_improvements)

plt.tight_layout()

plt.savefig('./figures/perturbative-improvement-pct.png')

plt.show()