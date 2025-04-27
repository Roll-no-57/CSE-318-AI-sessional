import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

def analyze_algorithm_combinations(filepath):
    # Read the CSV data
    df = pd.read_csv(filepath)
    
    # Calculate relative deviation for each row
    df['relative_deviation'] = (df['worst_cost'] - df['best_cost']) / df['average_cost']
    
    # Create figure with larger size for better readability
    plt.figure(figsize=(15, 8))
    
    # Create box plot with hue
    ax = sns.boxplot(
        x='perturbative_h', 
        y='relative_deviation',
        hue='constructive_h',
        data=df,
        palette='Set2'
    )
    
    # Customize the plot
    plt.title('Algorithm Combination Consistency Analysis\n(Lower values indicate more consistent results)', pad=20)
    plt.xlabel('Perturbative Algorithm')
    plt.ylabel('Relative Deviation ((worst-best)/avg)')
    
    # Rotate x-labels for better readability
    plt.xticks(rotation=0)
    
    # Add grid for better readability
    plt.grid(True, axis='y', linestyle='--', alpha=0.3)
    
    # Move legend outside of plot
    plt.legend(title='Constructive Algorithm', bbox_to_anchor=(1.05, 1), loc='upper left')
    
    # Calculate summary statistics
    summary_stats = df.groupby(['constructive_h', 'perturbative_h'])['relative_deviation'].agg([
        ('mean', 'mean'),
        ('std', 'std'),
        ('min', 'min'),
        ('max', 'max')
    ]).round(4)
    
    # Find the most and least consistent combinations
    most_consistent = df.groupby(['constructive_h', 'perturbative_h'])['relative_deviation'].mean().nsmallest(5)
    least_consistent = df.groupby(['constructive_h', 'perturbative_h'])['relative_deviation'].mean().nlargest(5)
    
    print("\nMost Consistent Algorithm Combinations (Lower is better):")
    for (cons, pert), value in most_consistent.items():
        print(f"{cons} + {pert}: {value:.4f}")
        
    print("\nLeast Consistent Algorithm Combinations (Higher is more variable):")
    for (cons, pert), value in least_consistent.items():
        print(f"{cons} + {pert}: {value:.4f}")
    
    # Calculate average deviation for each perturbative algorithm
    pert_stats = df.groupby('perturbative_h')['relative_deviation'].mean().sort_values()
    print("\nAverage Deviation by Perturbative Algorithm:")
    for pert, value in pert_stats.items():
        print(f"{pert}: {value:.4f}")
    
    # Calculate average deviation for each constructive algorithm
    cons_stats = df.groupby('constructive_h')['relative_deviation'].mean().sort_values()
    print("\nAverage Deviation by Constructive Algorithm:")
    for cons, value in cons_stats.items():
        print(f"{cons}: {value:.4f}")
    
    plt.tight_layout()
    return df, summary_stats

# Run the analysis
df_results, stats = analyze_algorithm_combinations('./results.csv')

# Create a heatmap of the combinations
plt.figure(figsize=(12, 8))
pivot_data = df_results.pivot_table(
    values='relative_deviation',
    index='constructive_h',
    columns='perturbative_h',
    aggfunc='mean'
)

sns.heatmap(pivot_data, 
            annot=True, 
            fmt='.3f', 
            cmap='YlOrRd_r',  # Reversed YlOrRd (yellow=high, red=low)
            cbar_kws={'label': 'Relative Deviation'})
plt.title('Algorithm Combination Consistency Heatmap\n(Lower values indicate more consistent results)')
plt.tight_layout()
plt.show()