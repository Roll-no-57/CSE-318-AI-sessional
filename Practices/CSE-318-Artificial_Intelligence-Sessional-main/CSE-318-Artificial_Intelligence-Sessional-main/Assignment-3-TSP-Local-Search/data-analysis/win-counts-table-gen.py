import pandas as pd

# Load data
df = pd.read_csv('./results.csv')

# Define metric
WIN_COUNT_METRIC = 'average_cost'

# Group by `constructive_heuristic` and `perturbative_heuristic`
summary = df.groupby(['problem_name', 'constructive_heuristic', 'perturbative_heuristic']).agg(
    best_cost_mean=('best_cost', 'mean'),
    average_cost_mean=('average_cost', 'mean'),
    worst_cost_mean=('worst_cost', 'mean')
).reset_index()

# Get the best-performing heuristic combination for each TSP file
best_heuristics = df.loc[df.groupby('problem_name')[WIN_COUNT_METRIC].idxmin()]

# Count occurrences of each (constructive_heuristic, perturbative_heuristic) combination
wins_count = best_heuristics.groupby(['constructive_heuristic', 'perturbative_heuristic']).size().reset_index(name='win_count')

# Combine the heuristic names into a single column for easier tabular display
wins_count['heuristic_combo'] = wins_count['constructive_heuristic'] + '+' + wins_count['perturbative_heuristic']

# Sort by win_count in ascending order
wins_count = wins_count.sort_values(by='win_count', ascending=False)

# Generate LaTeX table
latex_table = wins_count[['heuristic_combo', 'win_count']].to_latex(
    index=False, 
    header=['Heuristic Combination', 'Win Count'], 
    caption=f'Win Counts by Heuristic Combination',
    label='tab:win_counts',
    position='htbp'
)

# Save to a .tex file
with open('./win_counts_table.tex', 'w') as f:
    f.write(latex_table)

print("LaTeX table saved to './win_counts_table.tex'")
