import pandas as pd

def generate_latex_report(csv_file):
   # Read the CSV file
   df = pd.read_csv(csv_file)

   # Get summary statistics
   num_files = df['problem_name'].nunique()
   num_constructive = df['constructive_h'].nunique()
   num_perturbative = df['perturbative_h'].nunique()

   # Generate the latex report
   latex_report = f"""
\\documentclass{{article}}
\\usepackage[margin=1in]{{geometry}}
\\usepackage{{xcolor}}
\\usepackage{{amsmath}}

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
   for heuristic in df['constructive_h'].unique():
       latex_report += f"    \\item \\textbf{{{heuristic}}}: \\textcolor{{gray}}{{Description goes here}}\n"

   latex_report += """
\\end{itemize}

\\section*{Perturbative Heuristics}
\\begin{itemize}
"""

   # Add perturbative heuristics
   for heuristic in df['perturbative_h'].unique():
       latex_report += f"    \\item \\textbf{{{heuristic}}}: \\textcolor{{gray}}{{Description goes here}}\n"

   latex_report += """
\\end{itemize}

\\section*{Results}

"""


   latex_report += """
   
\\end{document}
"""

   return latex_report

# Example usage
latex_report = generate_latex_report('results.csv')

with open('main.tex','w') as f:
    f.write(latex_report)