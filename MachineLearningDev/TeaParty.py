from utils import *
from list_of_functions import *
import time
from tqdm import tqdm


process_dataset()

for name, model in tqdm(models):
    print(f"\n\nTraining model: {name}")
    start_time = time.time()
    model_pipeline(name, model)
    end_time = time.time()
    print(f"Training time: {int(end_time - start_time)} seconds")
voting_system()

pipeline.visualise_data()

for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")

    for name, model in models:
        pipeline.input_message = message
        predict_text(name, model)
        all_predictions.append((name, pipeline.prediction[0]))
    
    # Aggregate predictions 
    predict_text('VOTE: ', pipeline.votingClassifier)
    hard_voting(all_predictions)


