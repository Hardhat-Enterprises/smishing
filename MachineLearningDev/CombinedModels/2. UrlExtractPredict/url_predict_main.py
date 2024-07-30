from utils2 import *
from list_of_functions2 import *
import time
from tqdm import tqdm
from datasets import load_dataset

# This version loads online dataset from huggingface
# Maybe it's because the size of the ds, it's really slow 
# ----BEWARE WHAT MODELS YOU USE, SOME CAN TAKE HOURS----
# Utils2 is the same as utils but without class, so have to pass variables around
# Also because this dataset is not csv, it's parquet, so have to change the way to load it

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

