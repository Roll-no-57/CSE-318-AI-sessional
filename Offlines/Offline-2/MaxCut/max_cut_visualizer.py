import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

class MaxCutVisualizer:
    def __init__(self, csv_file):
        """
        Initialize visualizer with the CSV file containing MAX-CUT results.
        
        Args:
            csv_file: Path to the CSV file
        """
        self.df = pd.read_csv(csv_file)
        self.output_dir = "plots"
        
        # Create output directory if it doesn't exist
        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)
    
    def bar_chart_comparison(self, num_graphs=10, save=True):
        """
        Generate bar chart comparing algorithm performance for specified number of graphs.
        
        Args:
            num_graphs: Number of graphs to include
            save: Whether to save the plot
        """
        df_subset = self.df.head(num_graphs)
        
        # Extract graph names and algorithm values
        graph_names = df_subset['Name'].tolist()
        
        # Extract algorithm values
        randomized = df_subset['Simple Randomized'].tolist()
        greedy = df_subset['Simple Greedy'].tolist()
        semi_greedy = df_subset['Semi-greedy'].tolist()
        local_search = df_subset['Simple local Average value'].tolist()
        grasp = df_subset['GRASP Best value'].tolist()
        known_best = df_subset['Known best solution or upper bound'].tolist()
        
        # Convert any empty strings to NaN
        known_best = [float(x) if x and str(x).strip() else np.nan for x in known_best]
        
        plt.figure(figsize=(14, 8))
        
        # Set width and positions for bars
        bar_width = 0.15
        r1 = np.arange(len(graph_names))
        r2 = [x + bar_width for x in r1]
        r3 = [x + bar_width for x in r2]
        r4 = [x + bar_width for x in r3]
        r5 = [x + bar_width for x in r4]
        
        # Create bars
        plt.bar(r1, randomized, width=bar_width, label='Randomized', color='#4472C4')
        plt.bar(r2, greedy, width=bar_width, label='Greedy', color='#ED7D31')
        plt.bar(r3, semi_greedy, width=bar_width, label='Semi-Greedy', color='#A5A5A5')
        plt.bar(r4, grasp, width=bar_width, label='GRASP', color='#FFC000')
        plt.bar(r5, local_search, width=bar_width, label='Local Search', color='#5B9BD5')
        
        # Add line for known best solution (if available)
        for i, (x, y) in enumerate(zip(r1, known_best)):
            if not np.isnan(y):
                plt.plot([x - bar_width/2, x + 5*bar_width - bar_width/2], [y, y], 'r--', linewidth=1.5)
        
        # Add labels and title
        plt.xlabel('Graph Instances', fontweight='bold')
        plt.ylabel('Cut Value', fontweight='bold')
        plt.title(f'Max Cut Comparison (Graph 1-{num_graphs})', fontsize=16, fontweight='bold')
        
        # Add xticks
        center_positions = [r + 2*bar_width for r in r1]
        plt.xticks(center_positions, graph_names)
        
        # Create legend
        plt.legend(loc='upper center', bbox_to_anchor=(0.5, -0.08), ncol=5)
        
        # Set y-axis limits to include negative and positive values
        all_values = (
            randomized + greedy + semi_greedy + grasp + local_search +
            [x for x in known_best if not np.isnan(x)]
        )
        min_value = min(all_values, default=0)
        max_value = max(all_values, default=0)
        if min_value < 0:
            y_min = min_value * 1.1
        else:
            y_min = 0
        y_max = max_value * 1.1
        plt.ylim(y_min, y_max)
        
        plt.grid(axis='y', linestyle='--', alpha=0.7)
        plt.tight_layout()
        
        if save:
            filename = os.path.join(self.output_dir, f'max_cut_comparison_g1-{num_graphs}.png')
            plt.savefig(filename, dpi=300, bbox_inches='tight')
            print(f"Bar chart saved to {filename}")
        
        plt.show()
    
    def performance_ratio_chart(self, save=True):
        """
        Generate chart showing ratio of algorithm performance to known best solutions.
        Only includes graphs with known best solutions.
        """
        # Filter out rows with empty best known solution
        df_with_known = self.df.copy()
        df_with_known['Known best solution or upper bound'] = pd.to_numeric(
            df_with_known['Known best solution or upper bound'], errors='coerce')
        df_with_known = df_with_known.dropna(subset=['Known best solution or upper bound'])
        
        if len(df_with_known) == 0:
            print("No graphs with known best solutions found.")
            return
        
        # Calculate performance ratios (algorithm value / known best)
        df_with_known['Randomized Ratio'] = df_with_known['Simple Randomized'] / df_with_known['Known best solution or upper bound']
        df_with_known['Greedy Ratio'] = df_with_known['Simple Greedy'] / df_with_known['Known best solution or upper bound']
        df_with_known['Semi-greedy Ratio'] = df_with_known['Semi-greedy'] / df_with_known['Known best solution or upper bound']
        df_with_known['Local Search Ratio'] = df_with_known['Simple local Average value'] / df_with_known['Known best solution or upper bound']
        df_with_known['GRASP Ratio'] = df_with_known['GRASP Best value'] / df_with_known['Known best solution or upper bound']
        
        # Select columns for plotting
        ratio_columns = ['Randomized Ratio', 'Greedy Ratio', 'Semi-greedy Ratio', 'Local Search Ratio', 'GRASP Ratio']
        ratios = df_with_known[['Name'] + ratio_columns]
        
        # Set up the plot
        plt.figure(figsize=(14, 8))
        
        # Create the bar chart
        bar_width = 0.15
        graph_names = ratios['Name'].tolist()
        x = np.arange(len(graph_names))
        
        # Plot each algorithm
        plt.bar(x - 2*bar_width, ratios['Randomized Ratio'], width=bar_width, label='Randomized', color='#4472C4')
        plt.bar(x - bar_width, ratios['Greedy Ratio'], width=bar_width, label='Greedy', color='#ED7D31')
        plt.bar(x, ratios['Semi-greedy Ratio'], width=bar_width, label='Semi-Greedy', color='#A5A5A5')
        plt.bar(x + bar_width, ratios['GRASP Ratio'], width=bar_width, label='GRASP', color='#FFC000')
        plt.bar(x + 2*bar_width, ratios['Local Search Ratio'], width=bar_width, label='Local Search', color='#5B9BD5')
        
        # Add reference line at 1.0 (equal to known best)
        plt.axhline(y=1.0, color='r', linestyle='--', linewidth=1)
        
        # Add labels and title
        plt.xlabel('Graph Instances', fontweight='bold')
        plt.ylabel('Performance Ratio (Algorithm / Known Best)', fontweight='bold')
        plt.title('Algorithm Performance Relative to Known Best Solutions', fontsize=16, fontweight='bold')
        
        plt.xticks(x, graph_names)
        plt.legend(loc='upper center', bbox_to_anchor=(0.5, -0.08), ncol=5)
        
        # Set y-axis to include negative ratios
        all_ratios = ratios[ratio_columns].values.flatten()
        min_ratio = min(all_ratios, default=0)
        max_ratio = max(all_ratios, default=1)
        if min_ratio < 0:
            y_min = min_ratio * 1.1
        else:
            y_min = 0
        y_max = max_ratio * 1.1
        plt.ylim(y_min, y_max)
        
        plt.grid(axis='y', linestyle='--', alpha=0.7)
        plt.tight_layout()
        
        if save:
            filename = os.path.join(self.output_dir, 'performance_ratio.png')
            plt.savefig(filename, dpi=300, bbox_inches='tight')
            print(f"Performance ratio chart saved to {filename}")
        
        plt.show()
    
    def graph_size_analysis(self, save=True):
        """
        Analyze how algorithm performance varies with graph size
        """
        # Create a copy with numeric vertices and edges
        df_analysis = self.df.copy()
        df_analysis['|V|'] = pd.to_numeric(df_analysis['|V|'])
        df_analysis['|E|'] = pd.to_numeric(df_analysis['|E|'])
        
        # Create figure and axes for two subplots
        fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(18, 7))
        
        # Scatter plot: Vertices vs. Algorithm Performance
        ax1.scatter(df_analysis['|V|'], df_analysis['Simple Randomized'], label='Randomized', alpha=0.7)
        ax1.scatter(df_analysis['|V|'], df_analysis['Simple Greedy'], label='Greedy', alpha=0.7)
        ax1.scatter(df_analysis['|V|'], df_analysis['Semi-greedy'], label='Semi-Greedy', alpha=0.7)
        ax1.scatter(df_analysis['|V|'], df_analysis['GRASP Best value'], label='GRASP', alpha=0.7)
        ax1.set_xlabel('Number of Vertices (|V|)', fontweight='bold')
        ax1.set_ylabel('Cut Value', fontweight='bold')
        ax1.set_title('Algorithm Performance vs. Graph Size (Vertices)', fontweight='bold')
        ax1.legend()
        ax1.grid(True, linestyle='--', alpha=0.7)
        
        # Scatter plot: Edges vs. Algorithm Performance
        ax2.scatter(df_analysis['|E|'], df_analysis['Simple Randomized'], label='Randomized', alpha=0.7)
        ax2.scatter(df_analysis['|E|'], df_analysis['Simple Greedy'], label='Greedy', alpha=0.7)
        ax2.scatter(df_analysis['|E|'], df_analysis['Semi-greedy'], label='Semi-Greedy', alpha=0.7)
        ax2.scatter(df_analysis['|E|'], df_analysis['GRASP Best value'], label='GRASP', alpha=0.7)
        ax2.set_xlabel('Number of Edges (|E|)', fontweight='bold')
        ax2.set_ylabel('Cut Value', fontweight='bold')
        ax2.set_title('Algorithm Performance vs. Graph Size (Edges)', fontweight='bold')
        ax2.legend()
        ax2.grid(True, linestyle='--', alpha=0.7)
        
        # Set y-axis limits for both subplots
        all_values = (
            df_analysis['Simple Randomized'].tolist() +
            df_analysis['Simple Greedy'].tolist() +
            df_analysis['Semi-greedy'].tolist() +
            df_analysis['GRASP Best value'].tolist()
        )
        min_value = min(all_values, default=0)
        max_value = max(all_values, default=0)
        if min_value < 0:
            y_min = min_value * 1.1
        else:
            y_min = 0
        y_max = max_value * 1.1
        ax1.set_ylim(y_min, y_max)
        ax2.set_ylim(y_min, y_max)
        
        plt.tight_layout()
        
        if save:
            filename = os.path.join(self.output_dir, 'graph_size_analysis.png')
            plt.savefig(filename, dpi=300, bbox_inches='tight')
            print(f"Graph size analysis saved to {filename}")
        
        plt.show()
    
    def algorithm_comparison_boxplot(self, save=True):
        """
        Create boxplots to compare distribution of cut values across algorithms.
        """
        # Prepare data for boxplot
        data = {
            'Randomized': self.df['Simple Randomized'],
            'Greedy': self.df['Simple Greedy'],
            'Semi-greedy': self.df['Semi-greedy'],
            'Local Search': self.df['Simple local Average value'],
            'GRASP': self.df['GRASP Best value']
        }
        
        # Create figure
        plt.figure(figsize=(12, 8))
        
        # Create boxplot
        plt.boxplot([data[key] for key in data.keys()], labels=data.keys(), patch_artist=True)
        
        # Add labels and title
        plt.xlabel('Algorithm', fontweight='bold')
        plt.ylabel('Cut Value', fontweight='bold')
        plt.title('Distribution of Cut Values by Algorithm', fontsize=16, fontweight='bold')
        
        # Set y-axis limits
        all_values = np.concatenate([data[key] for key in data.keys()])
        min_value = min(all_values, default=0)
        max_value = max(all_values, default=0)
        if min_value < 0:
            y_min = min_value * 1.1
        else:
            y_min = 0
        y_max = max_value * 1.1
        plt.ylim(y_min, y_max)
        
        plt.grid(axis='y', linestyle='--', alpha=0.7)
        plt.tight_layout()
        
        if save:
            filename = os.path.join(self.output_dir, 'algorithm_boxplot.png')
            plt.savefig(filename, dpi=300, bbox_inches='tight')
            print(f"Algorithm comparison boxplot saved to {filename}")
        
        plt.show()
    
    def generate_all_visualizations(self):
        """
        Generate all visualization types
        """
        self.bar_chart_comparison(num_graphs=10)
        self.performance_ratio_chart()
        self.graph_size_analysis()
        self.algorithm_comparison_boxplot()

if __name__ == "__main__":
    # Change this to your CSV file path
    csv_file = "2105057.csv"
    
    try:
        visualizer = MaxCutVisualizer(csv_file)
        visualizer.generate_all_visualizations()
    except Exception as e:
        print(f"Error: {e}")