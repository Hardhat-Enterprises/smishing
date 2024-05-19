from utils2 import *
from list_of_functions2 import *
import time
from tqdm import tqdm
from datasets import load_dataset


url_dataset = load_dataset("kmack/Phishing_urls")
# Convert the splits of the dataset to pandas DataFrames
train_df = url_dataset['train'].to_pandas()
test_df = url_dataset['test'].to_pandas()
valid_df = url_dataset['valid'].to_pandas()

# Split the data into features (X) and labels (y)
X_train = train_df['text']
y_train = train_df['label']
X_test = test_df['text']
y_test = test_df['label']
X_valid = valid_df['text']
y_valid = valid_df['label']

# Extract features using TF-IDF
tfidf_vectorizer, X_train_features, X_test_features, X_valid_features = feature_extraction(X_train, X_test, X_valid)

# Train each model from models list
for name, model in tqdm(models):
    print(f"\n\nTraining model: {name}")
    start_time = time.time()
    model_pipeline(name, model, X_train_features, X_test_features, y_train, y_test)
    end_time = time.time()
    run_time = int(end_time - start_time)
    print(f"Training time: {run_time} seconds")
    keep_record(name, run_time, 'imported url')
# Train voting system on all the models
votingclassifier = voting_system(X_train_features, y_train, X_train_features, y_train, X_test_features, y_test)
keep_record('voting', None, 'voting system')

# Visualise model accuracies
visualise_data()

# Test predictions on sample messages using all models and display results
for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")
    print("Analysing URL")
    input_url = ' '.join(re.findall(r'(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)', message))
    print(input_url)
    if input_url != None:
        for name, model in models:
            start_time = time.time()
            prediction = predict_text(name, model, input_url, tfidf_vectorizer)
            end_time = time.time()
            run_time = end_time - start_time
            print(f"Predicting time: {run_time} seconds")
            all_predictions.append((name, prediction))
            keep_record(name, run_time, 'perdict time')
        
        # Two methods to vote on results
        # Aggregate predictions 
        predict_text('VOTE: ', votingclassifier, message, tfidf_vectorizer)
        hard_voting(all_predictions)





'''
# Use GridSearchCV to tune the parameters of your model
with parallel_backend('threading'):
    grid_search = GridSearchCV(model, param_grid, cv=5, return_train_score=True)
    grid_search.fit(X_valid, y_valid)


# Train each model from models list
for name, model in tqdm(models):
    print(f"\n\nTraining model: {name}")
    start_time = time.time()
    model_pipeline(name, model)
    end_time = time.time()
    run_time = int(end_time - start_time)
    print(f"Training time: {run_time} seconds")
    pipeline.keep_record(name, run_time)
# Train voting system on all the models
voting_system()

# Visualise model accuracies
pipeline.visualise_data()

# Test predictions on sample messages using all models and display results
for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")

    for name, model in models:
        pipeline.input_message = message
        predict_text(name, model)
        all_predictions.append((name, pipeline.prediction[0]))
    
    # Two methods to vote on results
    # Aggregate predictions 
    predict_text('VOTE: ', pipeline.votingClassifier)
    hard_voting(all_predictions)





# Store the best score and best parameters for each model
models_info[name]['best_param'] = grid_search.best_params_
models_info[name]['grid_score'] = grid_search.best_score_
print(f"Best parameters for {name}: {models_info[name]['best_param']}")
print(f"Best grid search score for {name}: {models_info[name]['grid_score']}")


df = pd.read_parquet('train.parquet')
pipeline.df = url_dataset.to_csv
print(pipeline.df)

#process_dataset(dataset_path=url_dataset)
pipeline.split_dataset()
pipeline.feature_extraction()
pipeline.balance_data()

pipeline.extract_urls()


pipeline.split_dataset("LINK")
url_vectorizer = pipeline.feature_extraction()
pipeline.balance_data()


# Train each model from models list
for name, model in tqdm(models):
    print(f"\n\nTraining model: {name}")
    start_time = time.time()
    model_pipeline(name, model)
    end_time = time.time()
    run_time = int(end_time - start_time)
    print(f"Training time: {run_time} seconds")
    pipeline.keep_record(name, run_time)
# Train voting system on all the models
voting_system()

# Visualise model accuracies
pipeline.visualise_data()

# Test predictions on sample messages using all models and display results
for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")

    for name, model in models:
        pipeline.input_message = message
        predict_text(name, model)
        all_predictions.append((name, pipeline.prediction[0]))
    
    # Two methods to vote on results
    # Aggregate predictions 
    predict_text('VOTE: ', pipeline.votingClassifier)
    hard_voting(all_predictions)



'''
