import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Load data
df = pd.read_csv('./results.csv')

# Group by `constructive_h` and `perturbative_h`
summary = df.groupby(['problem_name', 'constructive_h', 'perturbative_h']).agg(
    best_cost_mean=('best_cost', 'mean'),
    average_cost_mean=('average_cost', 'mean'),
    worst_cost_mean=('worst_cost', 'mean')
).reset_index()

# Get the best-performing heuristic combination for each TSP file
best_heuristics = df.loc[df.groupby('problem_name')['average_cost'].idxmin()]

print(best_heuristics)


# Plot best costs by heuristic
plt.figure(figsize=(12, 6))
sns.barplot(data=df, x='constructive_h', y='average_cost', hue='perturbative_h')
plt.title('Best Cost Comparison by Constructive and Perturbative Heuristics')
plt.xlabel('Constructive Heuristic')
plt.ylabel('Best Cost')
plt.legend(title='Perturbative Heuristic')
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig('best_cost_comparison.png')  # Save for LaTeX
plt.show()

# Box plot for average costs
plt.figure(figsize=(12, 6))
sns.boxplot(data=df, x='constructive_h', y='average_cost', hue='perturbative_h')
plt.title('Average Cost Distribution by Heuristic')
plt.xlabel('Constructive Heuristic')
plt.ylabel('Average Cost')
plt.legend(title='Perturbative Heuristic')
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig('average_cost_distribution.png')  # Save for LaTeX
plt.show()

