import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

WIN_COUNT_METRIC = 'average_cost'

# Load data
df = pd.read_csv('./results.csv')

# Group by `constructive_h` and `perturbative_h`
summary = df.groupby(['problem_name', 'constructive_h', 'perturbative_h']).agg(
    best_cost_mean=('best_cost', 'mean'),
    average_cost_mean=('average_cost', 'mean'),
    worst_cost_mean=('worst_cost', 'mean')
).reset_index()

# Get the best-performing heuristic combination for each TSP file
best_heuristics = df.loc[df.groupby('problem_name')[WIN_COUNT_METRIC].idxmin()]

# Count occurrences of each (constructive_h, perturbative_h) combination
wins_count = best_heuristics.groupby(['constructive_h', 'perturbative_h']).size().reset_index(name='win_count')

# Combine the heuristic names into a single column for easier plotting
wins_count['heuristic_combo'] = wins_count['constructive_h'] + '+' + wins_count['perturbative_h']

# Sort by win_count for better visualization
wins_count = wins_count.sort_values(by='win_count', ascending=False)

# Plot
plt.figure(figsize=(12, 8))
sns.barplot(data=wins_count, y='heuristic_combo', x='win_count', palette='viridis')
plt.xlabel('Number of Wins')
plt.ylabel('Heuristic Combination')
plt.title(f'Count of Wins ({WIN_COUNT_METRIC}) by Heuristic Combination')

# Improve layout
plt.tight_layout()

plt.show()
