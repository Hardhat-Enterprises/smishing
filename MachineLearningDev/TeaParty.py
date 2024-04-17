from utils import *
#from collections import Counter
from sklearn.neighbors import KNeighborsClassifier 
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import MultinomialNB  # Importing Naive Bayes
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC  # Import SVM

# List of models
models = [
    ("knn model", KNeighborsClassifier()), 
    ("decision tree", DecisionTreeClassifier()),
    ("logistic regression", LogisticRegression()),
    ("naive bayes", MultinomialNB()),
    ("random forest", RandomForestClassifier()),
    ("svc", SVC())
]

#sample_msg = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
sample_messages = [
    "Hello, you still have a fine that has not been paid. Please pay it in time, otherwise it will affect your travel. https://linkstps.xyz/au",
    "Eastlink: There is an outstanding debt on the toll invoice. Settlement should always be made before the maturity datae. https://tolls.eastlink.click/online",
    "We're unable to deliver your online package due to an address error. Please click promptly to update the address for re-delivery. https://ausorriso.xyz/i",
    "TODAY ONLY 40% off traditional and premium pizzas* ORDER NOW dominoes.au/7MKXrbmdRk T&Cs apply. To opt out send STOP to 0485865365",
    "Chemist Warehouse Fountain Gate sent you a Slyp receipt. View it here: https://reciepts.slyp.com.au/WRA-b94511379edf4de481438e-f4a6e952c0",
]

pipeline = ModelPipeline()

for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    pipeline.input_message = message
    print(f"Message: {message}")
    for name, model in models:
        pipeline.model = model
        pipeline.run_model_pipeline(0.2, 100, 1)
        all_predictions.append((name, pipeline.prediction[0]))

    # Aggregate predictions 
    voting(all_predictions)


