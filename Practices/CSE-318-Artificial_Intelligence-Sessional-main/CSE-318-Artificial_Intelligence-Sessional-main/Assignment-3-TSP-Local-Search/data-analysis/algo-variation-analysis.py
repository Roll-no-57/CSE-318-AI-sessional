import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

def analyze_algorithm_combinations(filepath):
    # Read the CSV data
    df = pd.read_csv(filepath)
    
    # Calculate relative deviation for each row
    df['relative_deviation'] = (df['worst_cost'] - df['best_cost']) / df['average_cost']
    
    # Calculate summary statistics
    summary_stats = df.groupby(['constructive_heuristic', 'perturbative_heuristic'])['relative_deviation'].agg([
        ('mean', 'mean'),
        ('std', 'std'),
        ('min', 'min'),
        ('max', 'max')
    ]).round(4)
    
    # Find the most and least consistent combinations
    most_consistent = df.groupby(['constructive_heuristic', 'perturbative_heuristic'])['relative_deviation'].mean().nsmallest(5)
    least_consistent = df.groupby(['constructive_heuristic', 'perturbative_heuristic'])['relative_deviation'].mean().nlargest(5)
    
    print("\nMost Consistent Algorithm Combinations (Lower is better):")
    for (cons, pert), value in most_consistent.items():
        print(f"{cons} + {pert}: {value:.4f}")
        
    print("\nLeast Consistent Algorithm Combinations (Higher is more variable):")
    for (cons, pert), value in least_consistent.items():
        print(f"{cons} + {pert}: {value:.4f}")
    
    # Calculate average deviation for each perturbative algorithm
    pert_stats = df.groupby('perturbative_heuristic')['relative_deviation'].mean().sort_values()
    print("\nAverage Deviation by Perturbative Algorithm:")
    for pert, value in pert_stats.items():
        print(f"{pert}: {value:.4f}")
    
    # Calculate average deviation for each constructive algorithm
    cons_stats = df.groupby('constructive_heuristic')['relative_deviation'].mean().sort_values()
    print("\nAverage Deviation by Constructive Algorithm:")
    for cons, value in cons_stats.items():
        print(f"{cons}: {value:.4f}")
    
    return df, summary_stats

# Run the analysis
df_results, stats = analyze_algorithm_combinations('./results.csv')

# Create a heatmap of the combinations
plt.figure(figsize=(12, 8))
pivot_data = df_results.pivot_table(
    values='relative_deviation',
    index='constructive_heuristic',
    columns='perturbative_heuristic',
    aggfunc='mean'
)

sns.heatmap(pivot_data, 
            annot=True, 
            fmt='.3f', 
            cmap='YlOrRd_r',  # Reversed YlOrRd (yellow=high, red=low)
            cbar_kws={'label': 'Relative Deviation'})

plt.title('Algorithm Combination Consistency Heatmap\n(Lower values indicate more consistent results)')
plt.tight_layout()

plt.savefig('./figures/algo-variation-analysis.png', dpi=300)

plt.show()