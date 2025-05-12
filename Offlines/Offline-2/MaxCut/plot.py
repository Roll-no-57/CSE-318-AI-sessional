import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

def generate_plot(csv_file, output_dir=None, num_graphs=10):
    """
    Generate a bar chart comparing algorithm performance for MAX-CUT problem, including negative cut values.

    Args:
        csv_file: Path to the CSV file containing the results
        output_dir: Directory to save the plot (if None, display only)
        num_graphs: Number of graphs to include in the plot
    """
    # Read the CSV file
    df = pd.read_csv(csv_file)

    # Filter to include only specified number of graphs
    df_subset = df.head(num_graphs)

    # Extract graph names for x-axis
    graph_names = df_subset['Name'].tolist()

    # Extract algorithm values
    randomized = df_subset['Simple Randomized'].tolist()
    greedy = df_subset['Simple Greedy'].tolist()
    semi_greedy = df_subset['Semi-greedy'].tolist()
    local_search = df_subset['Simple local Average value'].tolist()
    grasp = df_subset['GRASP Best value'].tolist()
    known_best = df_subset['Known best solution or upper bound'].tolist()

    # Convert any empty strings to NaN
    known_best = [float(x) if x else np.nan for x in known_best]

    # Create figure and axis
    plt.figure(figsize=(14, 8))

    # Set width of bars
    bar_width = 0.15

    # Set positions for bars
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
    plt.title(f'Max Cut (Graph 1-{num_graphs})', fontsize=16, fontweight='bold')

    # Add xticks on the middle of the group bars
    center_positions = [r + 2*bar_width for r in r1]
    plt.xticks(center_positions, graph_names)

    # Create legend
    plt.legend(loc='upper center', bbox_to_anchor=(0.5, -0.08), ncol=5)

    # Set y-axis limits to include negative and positive values with padding
    all_values = (
        randomized + greedy + semi_greedy + grasp + local_search +
        [x for x in known_best if not np.isnan(x)]
    )
    min_value = min(all_values, default=0)
    max_value = max(all_values, default=0)
    
    # Add padding: 10% below min and above max
    if min_value < 0:
        y_min = min_value * 1.1  # Extend below negative values
    else:
        y_min = 0  # Keep 0 as minimum if no negative values
    y_max = max_value * 1.1  # 10% padding above max
    
    plt.ylim(y_min, y_max)

    # Add grid for better readability
    plt.grid(axis='y', linestyle='--', alpha=0.7)

    # Tight layout
    plt.tight_layout()

    # Save plot if output directory provided
    if output_dir:
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
        plt.savefig(os.path.join(output_dir, 'max_cut_comparison.png'), dpi=300, bbox_inches='tight')
        print(f"Plot saved to {os.path.join(output_dir, 'max_cut_comparison.png')}")

    # Show plot
    plt.show()

if __name__ == "__main__":
    # Change these parameters as needed
    csv_file = "2105057.csv"  # Path to your CSV file
    output_dir = "plots"      # Directory to save the plot
    num_graphs = 10           # Number of graphs to include

    generate_plot(csv_file, output_dir, num_graphs)