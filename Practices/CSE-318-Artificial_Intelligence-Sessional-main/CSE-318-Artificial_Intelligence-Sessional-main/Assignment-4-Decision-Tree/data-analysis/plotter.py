import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import confusion_matrix
import numpy as np

# Read the CSV files
all_results_df = pd.read_csv('results/decision_tree_results.csv')
detailed_results_df = pd.read_csv('results/decision_tree_results_details.csv')

# Create results directory if it doesn't exist
import os
if not os.path.exists('plots'):
    os.makedirs('plots')

def plot_training_metrics(strategy, metric):
    """Create subplot of training metrics with different colors for given strategy and metric combination."""
    # Filter data for this strategy-metric combination
    mask = (all_results_df['selectionStrategy'] == strategy) & \
           (all_results_df['evaluationMetric'] == metric)
    data = all_results_df[mask]
    
    fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(15, 12))
    fig.suptitle(f'Training Metrics for {strategy} - {metric}')
    
    # Define colors for each subplot
    colors = ['#2ecc71', '#e74c3c', '#3498db', '#f1c40f']
    
    # Accuracy plot (green)
    sns.scatterplot(data=data, x='trainPct', y='accuracyPct', ax=ax1, color=colors[0])
    ax1.set_title('Training % vs Accuracy')
    ax1.set_xlabel('Training Percentage')
    ax1.set_ylabel('Accuracy %')
    
    # Construction time plot (red)
    sns.scatterplot(data=data, x='trainPct', y='constructionTime', ax=ax2, color=colors[1])
    ax2.set_title('Training % vs Construction Time')
    ax2.set_xlabel('Training Percentage')
    ax2.set_ylabel('Construction Time (ms)')
    
    # Node count plot (blue)
    sns.scatterplot(data=data, x='trainPct', y='nodeCount', ax=ax3, color=colors[2])
    ax3.set_title('Training % vs Node Count')
    ax3.set_xlabel('Training Percentage')
    ax3.set_ylabel('Number of Nodes')
    
    # Tree depth plot (yellow)
    sns.scatterplot(data=data, x='trainPct', y='treeDepth', ax=ax4, color=colors[3])
    ax4.set_title('Training % vs Tree Depth')
    ax4.set_xlabel('Training Percentage')
    ax4.set_ylabel('Tree Depth')
    
    plt.tight_layout()
    plt.savefig(f'plots/training_metrics_{strategy}_{metric}.png', dpi=200)
    plt.close()

def plot_combined_performance_boxplot():
    """Create combined boxplot of accuracy over runs for all strategy-metric combinations."""
    strategies = ['BEST', 'TOP_THREE']
    metrics = ['INFORMATION_GAIN', 'GINI_IMPURITY']
    
    # List to store accuracy data for all combinations
    all_accuracies = []
    
    # Calculate accuracies for each combination
    for strategy in strategies:
        for metric in metrics:
            mask = (detailed_results_df['selectionStrategy'] == strategy) & \
                   (detailed_results_df['evaluationMetric'] == metric)
            
            # Calculate accuracy per run
            run_accuracies = detailed_results_df[mask].groupby('run').apply(
                lambda x: (x['actualLabel'] == x['predictedLabel']).mean() * 100
            ).reset_index()
            run_accuracies.columns = ['run', 'accuracy']  # Renamed to match later reference
            
            # Add strategy and metric info
            run_accuracies['combination'] = f'{strategy}-{metric}'
            all_accuracies.append(run_accuracies)
    
    # Combine all data
    combined_data = pd.concat(all_accuracies)
    
    # Create plot
    plt.figure(figsize=(12, 6))
    
    # Define colors for each combination
    colors = ['#2ecc71', '#e74c3c', '#3498db', '#f1c40f']
    
    # Create boxplot with correct column name
    sns.boxplot(x='combination', y='accuracy', data=combined_data, palette=colors)
    
    plt.title('Accuracy Distribution Comparison Across All Combinations')
    plt.xlabel('Strategy-Metric Combination')
    plt.ylabel('Accuracy (%)')
    plt.xticks(rotation=45)
    
    # Add statistical annotations
    combinations = combined_data['combination'].unique()
    for i, comb in enumerate(combinations):
        comb_data = combined_data[combined_data['combination'] == comb]['accuracy']
        mean_acc = comb_data.mean()
        std_acc = comb_data.std()
        # plt.text(i, plt.ylim()[0], f'Mean: {mean_acc:.2f}%\nStd: {std_acc:.2f}', 
        #         ha='center', va='top', rotation=45)
    
    plt.tight_layout()
    plt.savefig('plots/combined_accuracy_distribution.png', dpi=200)
    plt.close()

def plot_attribute_depths(strategy, metric):
    """Create boxplot of attribute depths for given strategy and metric combination."""
    # Filter data for this strategy-metric combination
    mask = (all_results_df['selectionStrategy'] == strategy) & \
           (all_results_df['evaluationMetric'] == metric) & \
           (all_results_df['trainPct'] == 80)
    
    depth_cols = ['buyingDepth', 'maintDepth', 'doorsDepth', 
                 'personsDepth', 'lugBootDepth', 'safetyDepth']
    
    plt.figure(figsize=(12, 6))
    data_to_plot = all_results_df[mask][depth_cols]
    data_to_plot.columns = [col.replace('Depth', '') for col in depth_cols]
    
    sns.boxplot(data=data_to_plot)
    plt.title(f'Attribute Depths Distribution for {strategy} - {metric}')
    plt.xticks(rotation=45)
    plt.ylabel('Depth in Tree')
    plt.tight_layout()
    plt.savefig(f'plots/attribute_depths_{strategy}_{metric}.png', dpi=200)
    plt.close()

def plot_confusion_matrix(strategy, metric):
    """Create confusion matrix for given strategy and metric combination."""
    mask = (detailed_results_df['selectionStrategy'] == strategy) & \
           (detailed_results_df['evaluationMetric'] == metric)
    data = detailed_results_df[mask]
    
    # Create confusion matrix
    labels = ['unacc', 'acc', 'good', 'vgood']
    cm = confusion_matrix(data['actualLabel'], data['predictedLabel'], labels=labels)
    
    # Convert to percentage
    cm_pct = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis] * 100
    
    plt.figure(figsize=(10, 8))
    sns.heatmap(cm_pct, annot=True, fmt='.1f', xticklabels=labels, yticklabels=labels)
    plt.title(f'Confusion Matrix (%)\n{strategy} - {metric}')
    plt.xlabel('Predicted Label')
    plt.ylabel('True Label')
    plt.tight_layout()
    plt.savefig(f'plots/confusion_matrix_{strategy}_{metric}.png', dpi=200)
    plt.close()

def plot_combined_confusion_matrices():
    """Create combined confusion matrix plots for all strategy-metric combinations."""
    strategies = ['BEST', 'TOP_THREE']
    metrics = ['INFORMATION_GAIN', 'GINI_IMPURITY']
    labels = ['unacc', 'acc', 'good', 'vgood']
    
    # Different color palettes for each subplot
    color_maps = ['Greens', 'Reds', 'Blues', 'YlOrBr']
    
    # Create subplot grid
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    
    # Flatten axes for easier iteration
    axes_flat = axes.flatten()
    
    # Counter for subplot position
    plot_idx = 0
    
    for strategy in strategies:
        for metric in metrics:
            mask = (detailed_results_df['selectionStrategy'] == strategy) & \
                   (detailed_results_df['evaluationMetric'] == metric)
            data = detailed_results_df[mask]
            
            # Create confusion matrix
            cm = confusion_matrix(data['actualLabel'], data['predictedLabel'], labels=labels)
            
            # Convert to percentage
            cm_pct = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis] * 100
            
            # Create heatmap in current subplot
            ax = axes_flat[plot_idx]
            sns.heatmap(cm_pct, 
                       annot=True, 
                       fmt='.1f', 
                       xticklabels=labels, 
                       yticklabels=labels,
                       cmap=color_maps[plot_idx],
                       ax=ax)
            
            ax.set_title(f'{strategy} - {metric}')
            ax.set_xlabel('Predicted Label')
            ax.set_ylabel('True Label')
            
            # Rotate tick labels for better readability
            ax.set_xticklabels(ax.get_xticklabels(), rotation=45)
            ax.set_yticklabels(ax.get_yticklabels(), rotation=0)
            
            plot_idx += 1
    
    plt.suptitle('Confusion Matrices Across Different Combinations (%)', y=1.02, fontsize=14)
    plt.tight_layout()
    plt.savefig('plots/confusion_matrices_combined.png', dpi=200, bbox_inches='tight')
    plt.close()

def plot_error_rates(strategy, metric):
    """Create bar plot of error rates per class."""
    mask = (detailed_results_df['selectionStrategy'] == strategy) & \
           (detailed_results_df['evaluationMetric'] == metric)
    data = detailed_results_df[mask]
    
    # Calculate error rate per class
    error_rates = []
    labels = ['unacc', 'acc', 'good', 'vgood']
    
    for label in labels:
        class_data = data[data['actualLabel'] == label]
        error_rate = (class_data['actualLabel'] != class_data['predictedLabel']).mean() * 100
        error_rates.append(error_rate)
    
    plt.figure(figsize=(10, 6))
    plt.bar(labels, error_rates)
    plt.title(f'Error Rates per Class\n{strategy} - {metric}')
    plt.xlabel('Class')
    plt.ylabel('Error Rate (%)')
    plt.ylim(0, 100)
    
    # Add value labels on top of bars
    for i, v in enumerate(error_rates):
        plt.text(i, v + 1, f'{v:.1f}%', ha='center')
    
    plt.tight_layout()
    plt.savefig(f'plots/error_rates_{strategy}_{metric}.png', dpi=200)
    plt.close()


def plot_combined_error_rates():
    """Create combined error rate plots for all strategy-metric combinations."""
    strategies = ['BEST', 'TOP_THREE']
    metrics = ['INFORMATION_GAIN', 'GINI_IMPURITY']
    labels = ['unacc', 'acc', 'good', 'vgood']
    colors = ['#2ecc71', '#e74c3c', '#3498db', '#f1c40f']  # Different color for each subplot
    
    # Create subplot grid
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    
    # Flatten axes for easier iteration
    axes_flat = axes.flatten()
    
    # Counter for subplot position
    plot_idx = 0
    
    for strategy in strategies:
        for metric in metrics:
            mask = (detailed_results_df['selectionStrategy'] == strategy) & \
                   (detailed_results_df['evaluationMetric'] == metric)
            data = detailed_results_df[mask]
            
            # Calculate error rates
            error_rates = []
            for label in labels:
                class_data = data[data['actualLabel'] == label]
                error_rate = (class_data['actualLabel'] != class_data['predictedLabel']).mean() * 100
                error_rates.append(error_rate)
            
            # Create bar plot in current subplot
            ax = axes_flat[plot_idx]
            bars = ax.bar(labels, error_rates, color=colors[plot_idx])
            ax.set_title(f'{strategy} - {metric}')
            ax.set_xlabel('Class')
            ax.set_ylabel('Error Rate (%)')
            ax.set_ylim(0, 100)
            ax.tick_params(axis='x', rotation=45)
            
            # Add value labels on top of bars
            for bar in bars:
                height = bar.get_height()
                ax.text(bar.get_x() + bar.get_width()/2., height,
                       f'{height:.1f}%',
                       ha='center', va='bottom')
            
            plot_idx += 1
    
    plt.suptitle('Error Rates per Class Across Different Combinations', y=1.02, fontsize=14)
    plt.tight_layout()
    plt.savefig('plots/error_rates_combined.png', dpi=200, bbox_inches='tight')
    plt.close()


def plot_training_metrics_by_measure():
    """Create separate plots for each metric (accuracy, time, nodes, depth) showing all strategy combinations."""
    strategies = ['BEST', 'TOP_THREE']
    metrics = ['INFORMATION_GAIN', 'GINI_IMPURITY']
    colors = ['#2ecc71', '#e74c3c', '#3498db', '#f1c40f']  # Different color for each subplot
    
    # Plot for Accuracy
    plt.figure(figsize=(15, 12))
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    plt.suptitle('Accuracy Percentage Across Different Combinations', y=1.02, fontsize=14)
    axes_flat = axes.flatten()
    plot_idx = 0
    
    for strategy in strategies:
        for metric in metrics:
            mask = (all_results_df['selectionStrategy'] == strategy) & \
                   (all_results_df['evaluationMetric'] == metric)
            data = all_results_df[mask]
            
            ax = axes_flat[plot_idx]
            sns.scatterplot(data=data, x='trainPct', y='accuracyPct', ax=ax, color=colors[plot_idx])
            ax.set_title(f'{strategy} - {metric}')
            ax.set_xlabel('Training Percentage')
            ax.set_ylabel('Accuracy %')
            plot_idx += 1
    
    plt.tight_layout()
    plt.savefig('plots/training_accuracy_combined.png', dpi=200, bbox_inches='tight')
    plt.close()
    
    # Plot for Construction Time
    plt.figure(figsize=(15, 12))
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    plt.suptitle('Construction Time Across Different Combinations', y=1.02, fontsize=14)
    axes_flat = axes.flatten()
    plot_idx = 0
    
    for strategy in strategies:
        for metric in metrics:
            mask = (all_results_df['selectionStrategy'] == strategy) & \
                   (all_results_df['evaluationMetric'] == metric)
            data = all_results_df[mask]
            
            ax = axes_flat[plot_idx]
            sns.scatterplot(data=data, x='trainPct', y='constructionTime', ax=ax, color=colors[plot_idx])
            ax.set_title(f'{strategy} - {metric}')
            ax.set_xlabel('Training Percentage')
            ax.set_ylabel('Construction Time (ms)')
            plot_idx += 1
    
    plt.tight_layout()
    plt.savefig('plots/training_time_combined.png', dpi=200, bbox_inches='tight')
    plt.close()
    
    # Plot for Node Count
    plt.figure(figsize=(15, 12))
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    plt.suptitle('Node Count Across Different Combinations', y=1.02, fontsize=14)
    axes_flat = axes.flatten()
    plot_idx = 0
    
    for strategy in strategies:
        for metric in metrics:
            mask = (all_results_df['selectionStrategy'] == strategy) & \
                   (all_results_df['evaluationMetric'] == metric)
            data = all_results_df[mask]
            
            ax = axes_flat[plot_idx]
            sns.scatterplot(data=data, x='trainPct', y='nodeCount', ax=ax, color=colors[plot_idx])
            ax.set_title(f'{strategy} - {metric}')
            ax.set_xlabel('Training Percentage')
            ax.set_ylabel('Number of Nodes')
            plot_idx += 1
    
    plt.tight_layout()
    plt.savefig('plots/training_nodes_combined.png', dpi=200, bbox_inches='tight')
    plt.close()
    
    # Plot for Tree Depth
    plt.figure(figsize=(15, 12))
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    plt.suptitle('Tree Depth Across Different Combinations', y=1.02, fontsize=14)
    axes_flat = axes.flatten()
    plot_idx = 0
    
    for strategy in strategies:
        for metric in metrics:
            mask = (all_results_df['selectionStrategy'] == strategy) & \
                   (all_results_df['evaluationMetric'] == metric)
            data = all_results_df[mask]
            
            ax = axes_flat[plot_idx]
            sns.scatterplot(data=data, x='trainPct', y='treeDepth', ax=ax, color=colors[plot_idx])
            ax.set_title(f'{strategy} - {metric}')
            ax.set_xlabel('Training Percentage')
            ax.set_ylabel('Tree Depth')
            plot_idx += 1
    
    plt.tight_layout()
    plt.savefig('plots/training_depth_combined.png', dpi=200, bbox_inches='tight')
    plt.close()


# Update main function
def main():
    strategies = ['BEST', 'TOP_THREE']
    metrics = ['INFORMATION_GAIN', 'GINI_IMPURITY']
    
    # Generate combined performance boxplot
    print("Generating combined performance boxplot...")
    plot_combined_performance_boxplot()

    # Generate combined error rates plot
    print("Generating combined error rates plot...")
    plot_combined_error_rates()

    # Generate combined confusion matrices plot
    print("Generating combined confusion matrices plot...")
    plot_combined_confusion_matrices()

    # Generate combined training metrics plots
    print("Generating combined training metrics plots...")
    plot_training_metrics_by_measure()
    
    for strategy in strategies:
        for metric in metrics:
            print(f"Generating plots for {strategy} - {metric}...")
            # plot_training_metrics(strategy, metric)
            plot_attribute_depths(strategy, metric)
            # plot_confusion_matrix(strategy, metric)
            # plot_error_rates(strategy, metric)
    
    print("All plots have been generated in the 'plots' directory!")

if __name__ == "__main__":
    main()