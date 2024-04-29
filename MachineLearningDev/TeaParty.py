from utils import *
#from collections import Counter
from sklearn.neighbors import KNeighborsClassifier 
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import MultinomialNB  # Importing Naive Bayes
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC  # Import SVM

def what_mail(prediction):
    if prediction == 0:
        print('Ham mail')
    elif prediction == 1:
        print('Smishing Mail')
    else:
        print('Spam Mail')


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
print("Let's vote: ")
for name, model in models:
    pipeline = ModelPipeline(model)
    pipeline.input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD'
                              "Hello, you still have a fine that has not been paid. Please pay it in time, otherwise it will affect your travel. https://linkstps.xyz/au",
                              "Eastlink: There is an outstanding debt on the toll invoice. Settlement should always be made before the maturity datae. https://tolls.eastlink.click/online",
                              "We're unable to deliver your online package due to an address error. Please click promptly to update the address for re-delivery. https://ausorriso.xyz/i",
                              "TODAY ONLY 40% off traditional and premium pizzas* ORDER NOW dominoes.au/7MKXrbmdRk T&Cs apply. To opt out send STOP to 0485865365",
                              "Chemist Warehouse Fountain Gate sent you a Slyp receipt. View it here: https://reciepts.slyp.com.au/WRA-b94511379edf4de481438e-f4a6e952c0",]
    pipeline.run_model_pipeline(0.2, 100, 1)
    all_predictions.append((name, pipeline.prediction[0]))

    for name, model in models:
        pipeline.input_message = message
        predict_text(name, model, True)
        all_predictions.append((name, pipeline.prediction[0]))
    
    # Two methods to vote on results
    # Aggregate predictions 
    predict_text('VOTE: ', pipeline.votingClassifier)
    hard_voting(all_predictions)

# Count occurrences of each prediction
counts = {0: 0, 1: 0, 2: 0}
for name, prediction in all_predictions:
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

