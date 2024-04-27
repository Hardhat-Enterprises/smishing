from utils import *
from scipy.sparse import csr_matrix, hstack, vstack

pipeline = ModelPipeline()



# Data:
#sample_messages
# List:
#models
# Dictionaries:
#param_grid
#models_info: - name, model, param_grid, best_params, grid_score, cross_val_score, weight

# Load and process the dataset
def process_dataset():
    pipeline.load_dataset()
    
    pipeline.split_dataset()
    pipeline.feature_extraction()
    
    #pipeline.merge_url_feature()
    # PCA optional
    #pipeline.dimensionality_reduction()    
    pipeline.balance_data()
    return pipeline.X_train_features, pipeline.X_test_features, pipeline.y_train, pipeline.y_test

# Train the models
def tune_train_model(name, model):
    pipeline.param_tuning(name, model)
    pipeline.train_model(name, model)

# Evaluate the model: accuracy, precision, recall, f1-score
# pipeline.evaluate_model(model)

# Dataset and model for one model
def model_pipeline(name, model):
    tune_train_model(name, model)
    pipeline.evaluate_model(name, model)


# Multi models cross-validation and voting system
# Use model list
def voting_system():
    pipeline.cross_validation()
    pipeline.train_voting_model()
    pipeline.evaluate_model('voting', pipeline.votingClassifier)


# Predict input text and print results
def predict_text(name, model):
    pipeline.make_predict(model)
    pipeline.get_result()
    print(name.ljust(40), pipeline.prediction, pipeline.result)


