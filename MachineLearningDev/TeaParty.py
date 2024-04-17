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

# Initialise empty predictions list
all_predictions = []
for name, model in models:
    pipeline = ModelPipeline(model)
    pipeline.input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
    pipeline.run_model_pipeline(0.2, 100, 1)
    all_predictions.append((name, pipeline.prediction[0]))

# Aggregate predictions 
voting(all_predictions)
