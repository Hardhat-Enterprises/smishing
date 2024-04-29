from utils import *
from list_of_functions import *
import time
from tqdm import tqdm


# Process dataset
process_dataset(True)

# Train each model from models list
for name, model in tqdm(models):
    print(f"\n\nTraining model: {name}")
    start_time = time.time()
    model_pipeline(name, model)
    end_time = time.time()
    print(f"Training time: {int(end_time - start_time)} seconds")
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




