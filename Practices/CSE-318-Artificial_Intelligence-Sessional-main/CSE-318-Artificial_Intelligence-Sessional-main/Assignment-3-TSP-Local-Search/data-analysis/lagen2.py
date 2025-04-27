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
       x='perturbative_heuristic', 
       y='relative_deviation',
       hue='constructive_heuristic',
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
   
   plt.tight_layout()
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

\\title{{CSE 318 - Assignment 3}}
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
       latex_report += f"    \\item \\textbf{{{heuristic}}}\n"

   latex_report += """
\\end{itemize}

\\section*{Perturbative Heuristics}
\\begin{itemize}
"""

   # Add perturbative heuristics
   for heuristic in df['perturbative_heuristic'].unique():
       latex_report += f"    \\item \\textbf{{{heuristic}}}\n"

   latex_report += """
\\end{itemize}

"""

   latex_report += """

\\newpage
   
\\section*{Results}

In the following tables and figures, the relative performances of the constructive and perturbative algorithms are analyzed according to mean values over 10 test runs for each combinations. 

\\begin{figure}[h]
\\centering
\\includegraphics[width=\\textwidth]{figures/count-of-wins-comparison.png}
\\caption{Count of Wins (average wins) by Heuristic Combination}
\\end{figure}


\\begin{figure}[h]
\\centering
\\includegraphics[width=\\textwidth]{figures/perturbative-improvement-pct.png}
\\caption{Improvement Percentages by Perturbative Algorithm}
\\end{figure}

"""

   latex_report += """

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

       "./figures/perturbative-improvement-pct.png"

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

\\end{document}
"""

   return latex_report

# Run the analysis
df_results, stats = analyze_algorithm_combinations('./results.csv')

# Generate the LaTeX report
latex_report = generate_latex_report('./results.csv', df_results, stats)

with open('main.tex', 'w') as f:
   f.write(latex_report)