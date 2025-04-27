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
    """Create subplot of training metrics for given strategy and metric combination."""
    # Filter data for this strategy-metric combination
    mask = (all_results_df['selectionStrategy'] == strategy) & \
           (all_results_df['evaluationMetric'] == metric)
    data = all_results_df[mask]
    
    fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(15, 12))
    fig.suptitle(f'Training Metrics for {strategy} - {metric}')
    
    # Accuracy plot
    sns.scatterplot(data=data, x='trainPct', y='accuracyPct', ax=ax1)
    ax1.set_title('Training % vs Accuracy')
    ax1.set_xlabel('Training Percentage')
    ax1.set_ylabel('Accuracy %')
    
    # Construction time plot
    sns.scatterplot(data=data, x='trainPct', y='constructionTime', ax=ax2)
    ax2.set_title('Training % vs Construction Time')
    ax2.set_xlabel('Training Percentage')
    ax2.set_ylabel('Construction Time (ms)')
    
    # Node count plot
    sns.scatterplot(data=data, x='trainPct', y='nodeCount', ax=ax3)
    ax3.set_title('Training % vs Node Count')
    ax3.set_xlabel('Training Percentage')
    ax3.set_ylabel('Number of Nodes')
    
    # Tree depth plot
    sns.scatterplot(data=data, x='trainPct', y='treeDepth', ax=ax4)
    ax4.set_title('Training % vs Tree Depth')
    ax4.set_xlabel('Training Percentage')
    ax4.set_ylabel('Tree Depth')
    
    plt.tight_layout()
    plt.savefig(f'plots/training_metrics_{strategy}_{metric}.png')
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
    plt.savefig(f'plots/attribute_depths_{strategy}_{metric}.png')
    plt.close()

def plot_performance_boxplot(strategy, metric):
    """Create boxplot of accuracy over runs for given strategy and metric combination."""
    mask = (detailed_results_df['selectionStrategy'] == strategy) & \
           (detailed_results_df['evaluationMetric'] == metric)
    
    # Calculate accuracy per run
    run_accuracies = detailed_results_df[mask].groupby('run').apply(
        lambda x: (x['actualLabel'] == x['predictedLabel']).mean() * 100
    ).reset_index()
    run_accuracies.columns = ['run', 'accuracy']
    
    plt.figure(figsize=(8, 6))
    sns.boxplot(y=run_accuracies['accuracy'])
    plt.title(f'Accuracy Distribution over Runs\n{strategy} - {metric}')
    plt.ylabel('Accuracy (%)')
    plt.tight_layout()
    plt.savefig(f'plots/accuracy_distribution_{strategy}_{metric}.png')
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
    plt.savefig(f'plots/confusion_matrix_{strategy}_{metric}.png')
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
    plt.savefig(f'plots/error_rates_{strategy}_{metric}.png')
    plt.close()

def main():
    strategies = ['BEST', 'TOP_THREE']
    metrics = ['INFORMATION_GAIN', 'GINI_IMPURITY']
    
    for strategy in strategies:
        for metric in metrics:
            print(f"Generating plots for {strategy} - {metric}...")
            plot_training_metrics(strategy, metric)
            plot_attribute_depths(strategy, metric)
            plot_performance_boxplot(strategy, metric)
            plot_confusion_matrix(strategy, metric)
            plot_error_rates(strategy, metric)
    
    print("All plots have been generated in the 'plots' directory!")

if __name__ == "__main__":
    main()