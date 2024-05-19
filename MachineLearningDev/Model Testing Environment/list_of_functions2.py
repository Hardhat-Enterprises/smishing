from utils2 import *
from scipy.sparse import csr_matrix, hstack, vstack


# Data:
#sample_messages
# List:
#models
# Dictionaries:
#param_grid
#models_info: - name, model, param_grid, best_params, grid_score, cross_val_score, weight, evaluation

# Load and process the dataset, true or false for dimensionality reduction
def process_dataset(y_train, X_train_features, reduce=False, dataset_path='DatasetCombined.csv'):
    load_csv(dataset_path)
    #extract_urls()
    split_dataset()
    feature_extraction()
    
    #merge_url_feature()
    if reduce:
        # reduce dimensionality optional
        dimensionality_reduction()    

    balance_data(y_train, X_train_features)

    #split_text_and_link()
    #return X_train_features, X_test_features, y_train, y_test

# Train the models
def tune_train_model(name, model, X_train_features, y_train):
    param_tuning(name, model, X_train_features, y_train)
    train_model(name, model, X_train_features, y_train)
    return models_info[name]['model']

# Evaluate the model: accuracy, precision, recall, f1-score
# evaluate_model(model)

# Dataset and model for one model
def model_pipeline(name, model, X_train_features, X_test_features, y_train, y_test):
    tune_train_model(name, model, X_train_features, y_train)
    train_accuracy, test_accuracy, precision, recall, f1 = evaluate_model(name, model, X_train_features, X_test_features, y_train, y_test)
    return train_accuracy, test_accuracy, precision, recall, f1


# Multi models cross-validation and voting system
# Use model list
def voting_system(X_features, y, X_train_features, y_train, X_test_features, y_test):
    cross_validation(X_features, y)
    votingClassifier = train_voting_model(X_train_features, y_train)
    evaluate_model('voting', votingClassifier, X_train_features, X_test_features, y_train, y_test)
    return votingClassifier


# Predict input text and print results, true or false for dimension reduction
def predict_text(name, model, input_message, tfidf_vectorizer):
    prediction = make_predict(model, input_message, tfidf_vectorizer, reduce=False)
    result = get_result(prediction)
    print(name.ljust(40), prediction, result)
    return prediction


