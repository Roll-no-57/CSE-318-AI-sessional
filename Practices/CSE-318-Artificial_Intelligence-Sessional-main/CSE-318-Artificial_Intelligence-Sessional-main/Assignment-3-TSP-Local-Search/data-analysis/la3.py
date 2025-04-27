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


def generate_latex_report(csv_file, algorithm_analysis_df, algorithm_analysis_stats):
    # Read the CSV file
    df = pd.read_csv(csv_file)

    # Get summary statistics
    num_files = df['problem_name'].nunique()
    num_constructive = df['constructive_heuristic'].nunique()
    num_perturbative = df['perturbative_heuristic'].nunique()

    # Generate the latex report
    latex_report = f"""
\\documentclass{{article}}
\\usepackage[margin=1in]{{geometry}}
\\usepackage{{xcolor}}
\\usepackage{{amsmath}}
\\usepackage{{booktabs}}
\\usepackage{{graphicx}}

\\title{{TSP Problem Report}}
\\author{{Anik Saha - 2005001}}

\\begin{{document}}
\\maketitle

This report summarizes the results of running various constructive and perturbative heuristics on the Traveling Salesman Problem (TSP) for the \\textbf{{{', '.join(df['problem_name'].unique())}}} problem instances.

\\section*{{Summary Statistics}}
\\begin{{itemize}}
  \\item Total number of TSP problem files: {num_files}
  \\item Total number of constructive heuristics: {num_constructive}
  \\item Total number of perturbative heuristics: {num_perturbative}
\\end{{itemize}}

\\section*{{Constructive Heuristics}}
\\begin{{itemize}}
"""

    # Add constructive heuristics
    for heuristic in df['constructive_heuristic'].unique():
        latex_report += f"    \\item \\textbf{{{heuristic}}}: \\textcolor{{gray}}{{Description goes here}}\n"

    latex_report += """
\\end{itemize}

\\section*{Perturbative Heuristics}
\\begin{itemize}
"""

    # Add perturbative heuristics
    for heuristic in df['perturbative_heuristic'].unique():
        latex_report += f"    \\item \\textbf{{{heuristic}}}: \\textcolor{{gray}}{{Description goes here}}\n"

    latex_report += """
\\end{itemize}

\\section*{Results}
\\begin{table}[h]
\\centering
\\begin{tabular}{lllrrr}
\\toprule
Problem & Constructive & Perturbative & Best Cost & Average Cost & Worst Cost \\\\
\\midrule
"""

    # Add table rows
    for _, row in df.iterrows():
        latex_report += f"{row['problem_name']} & {row['constructive_heuristic']} & {row['perturbative_heuristic']} & {row['best_cost']:.2f} & {row['average_cost']:.2f} & {row['worst_cost']:.2f} \\\\\n"

    latex_report += """
\\bottomrule
\\end{tabular}
\\caption{TSP Results}
\\end{table}

\\section*{Algorithm Combination Consistency Analysis}
\\begin{table}[h]
\\centering
\\begin{tabular}{llc}
\\toprule
Constructive & Perturbative & Mean Deviation \\\\
\\midrule
"""

    # Add summary statistics table
    for (cons, pert), row in algorithm_analysis_stats.iterrows():
        latex_report += f"{cons} & {pert} & {row['mean']:.4f} \\\\\n"

    latex_report += """
\\bottomrule
\\end{tabular}
\\caption{Algorithm Combination Consistency Analysis}
\\end{table}

\\begin{figure}[h]
\\centering
\\includegraphics[width=\\textwidth]{figures/algo-variation-analysis.png}
\\caption{Algorithm Combination Consistency Heatmap (Lower values indicate more consistent results)}
\\end{figure}

\\section*{Win Count Analysis}
\\begin{table}[h]
\\centering
\\begin{tabular}{llr}
\\toprule
Constructive & Perturbative & Win Count \\\\
\\midrule
"""

    # Add win count table
    wins_count = algorithm_analysis_df.groupby(['constructive_heuristic', 'perturbative_heuristic'])['win_count'].sum().reset_index()
    for _, row in wins_count.iterrows():
        latex_report += f"{row['constructive_heuristic']} & {row['perturbative_heuristic']} & {row['win_count']} \\\\\n"

    latex_report += """
\\bottomrule
\\end{tabular}
\\caption{Win Counts by Heuristic Combination}
\\end{table}

\\begin{figure}[h]
\\centering
\\includegraphics[width=\\textwidth]{figures/count-of-wins-comparison.png}
\\caption{Count of Wins by Heuristic Combination (Sorted by Win Count)}
\\end{figure}

\\end{document}
"""

    return latex_report

# Run the analysis
df_results, stats = analyze_algorithm_combinations('./results.csv')

# Generate the LaTeX report
latex_report = generate_latex_report('./results.csv', df_results, stats)

with open('main.tex', 'w') as f:
    f.write(latex_report)
