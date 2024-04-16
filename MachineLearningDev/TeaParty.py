 import logistic_regression_model as lreg
import decision_tree_model as dtree
import knn_model as knn
import random_forest_model as rforest
import svm_model as svm
import naive_bayes_model as nbayes

prediction = logistic_regression_model.prediction
print(prediction)
#Example input message
input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']

# Make predictions using each model
predictions_logistic_regression = predict_logistic_regression(input_message)
predictions_decision_tree = predict_decision_tree(input_message)
predictions_knn = predict_knn(input_message)
predictions_random_forest = predict_random_forest(input_message)
predictions_svm = predict_svm(input_message)
predictions_naive_bayes = predict_naive_bayes(input_message)

# Aggregate predictions
all_predictions = [
    predictions_logistic_regression,
    predictions_decision_tree,
    predictions_knn,
    predictions_random_forest,
    predictions_svm,
    predictions_naive_bayes
]

# Output all predictions
print("All Predictions:")
for i, prediction in enumerate(all_predictions):
    print(f"Model {i+1}: {prediction}")

# Count occurrences of each prediction
counts = {0: 0, 1: 0, 2: 0}
for prediction in all_predictions:
    counts[prediction] += 1

# Determine the most common prediction
most_common_prediction = max(counts, key=counts.get)

# Output the most common prediction
if most_common_prediction == 0:
    print('Final Decision: Ham mail')
elif most_common_prediction == 1:
    print('Final Decision: Smishing Mail')
else:
    print('Final Decision: Spam Mail')

'''    

def aggregate_predictions(predictions):
    # Count occurrences of each prediction
    counts = {0: 0, 1: 0, 2: 0}  # Initialize counts for 0, 1, and 2
    for prediction in predictions:
        counts[prediction] += 1
    
    # Determine the most common prediction
    max_count = max(counts.values())
    most_common_prediction = [key for key, value in counts.items() if value == max_count][0]
    
    return most_common_prediction

def main():    
    #Aggregate predictions
    aggregated_prediction = aggregate_predictions([logistic_regression_model.prediction, decision_tree_model.prediction])

    

    # Output the aggregated prediction
    if aggregated_prediction == 0:
        print('Ham mail')
    elif aggregated_prediction == 1:
        print('Smishing Mail')
    else:
        print('Spam Mail')
'''
# Run the main function
#if __name__ == "__main__":
#    main()