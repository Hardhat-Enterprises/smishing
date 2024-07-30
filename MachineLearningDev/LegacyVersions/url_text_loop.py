from utils import *
from list_of_functions import *
import time
from tqdm import tqdm

# Separate url and text and loop them separately

# Process dataset
pipeline.load_dataset()
pipeline.extract_urls()

text_models_info = url_models_info = models_info
'''
# Train text model
pipeline.split_dataset()
text_vectorizer = pipeline.feature_extraction()
pipeline.balance_data()
# Train each model from models list
text_models=[]
models_info = text_models_info
for name, model in tqdm(models):
    print(f"\n\nTraining model: {name}")
    start_time = time.time()
    model_instance = model_pipeline(name, model)
    end_time = time.time()
    run_time = int(end_time - start_time)
    print(f"Training time: {run_time} seconds")
    text_models.append((name, model_instance))
    print(name, str(model_instance))
    pipeline.keep_record(name, run_time)
# Train voting system on all the models
model_instance = voting_system()
text_models.append(('voting', model_instance))
text_models_info= models_info
pipeline.visualise_data()
'''
# Train url model
pipeline.split_dataset("LINK")
url_vectorizer = pipeline.feature_extraction()
pipeline.balance_data()
url_models=[]
models_info = url_models_info
for name, model in tqdm(models):
    print(f"\n\nTraining model: {name}")
    start_time = time.time()
    model_instance = model_pipeline(name, model)
    end_time = time.time()
    run_time = int(end_time - start_time)
    print(f"Training time: {run_time} seconds")
    url_models.append((name, model_instance))
    pipeline.keep_record(name, run_time)
# Train voting system on all the models
model_instance = voting_system()
url_models.append(('voting', model_instance))
url_models_info = models_info
# Visualise model accuracies
pipeline.visualise_data()


# Test predictions on sample messages using all models and display results
for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")
    '''
    models_info = text_models_info
    for name, model in text_models:
        pipeline.tfidf_vectorizer = text_vectorizer
        pipeline.input_message = message
        predict_text(name, model)
        all_predictions.append((name, pipeline.prediction[0]))
    # Two methods to vote on results
    # Aggregate predictions 
    predict_text('VOTE: ', pipeline.votingClassifier)
    hard_voting(all_predictions)
    '''
    print("Analysing URL")
    input_url = ' '.join(re.findall(r'(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)', message))
    print(input_url)
    if input_url != None:
        models_info = url_models_info
        for name, model in url_models:
            print(f"Model: {name}")
            pipeline.tfidf_vectorizer = url_vectorizer
            pipeline.input_message = input_url
            predict_text(name, model)
            all_predictions.append((name, pipeline.prediction[0]))
        # Two methods to vote on results
        # Aggregate predictions 
        predict_text('VOTE: ', pipeline.votingClassifier)
        hard_voting(all_predictions)



