#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Apr 18 10:18:39 2024

@author: gladizgregory2
"""

import pandas as pd
from sklearn.model_selection import train_test_split, RandomizedSearchCV
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from scipy.stats import randint

# Load data
df = pd.read_csv('/Users/gladizgregory2/desktop/dataset.csv')

# Create a dictionary
map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
df['LABEL'] = df['LABEL'].map(map_label)

# Split data into features (X) and labels (y)
X = df['TEXT']
y = df['LABEL']

# Split data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=100)

# Feature extraction using TF-IDF
tfidf_vectorizer = TfidfVectorizer(min_df=1, stop_words='english', lowercase=True)
X_train_features = tfidf_vectorizer.fit_transform(X_train)
X_test_features = tfidf_vectorizer.transform(X_test)

# Set up the parameters distribution for random search
param_dist = {
    'n_estimators': randint(50, 200),
    'max_depth': [None] + list(randint(1, 50).rvs(10)),
    'min_samples_split': randint(2, 11),
    'min_samples_leaf': randint(1, 11),
}

# Initialize Random Forest classifier
model = RandomForestClassifier()

# Initialize RandomizedSearchCV
random_search = RandomizedSearchCV(estimator=model, param_distributions=param_dist, n_iter=100, cv=5, scoring='accuracy', random_state=42)

# Fit the random search to the data
random_search.fit(X_train_features, y_train)

# Get the best parameters and the best score
best_params = random_search.best_params_
best_score = random_search.best_score_

print("Best Parameters:", best_params)
print("Best Accuracy:", best_score)

# Train a new model with the best parameters
best_rf_model = RandomForestClassifier(**best_params)
best_rf_model.fit(X_train_features, y_train)

# Evaluate the model
train_accuracy = accuracy_score(y_train, best_rf_model.predict(X_train_features))
test_accuracy = accuracy_score(y_test, best_rf_model.predict(X_test_features))

print('Accuracy on training data: ', train_accuracy)
print('Accuracy on test data: ', test_accuracy)

# Example prediction
input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
input_message_features = tfidf_vectorizer.transform(input_message)
prediction = best_rf_model.predict(input_message_features)

if prediction[0] == 0:
    print('Ham mail')
elif prediction[0] == 1:
    print('Smishing Mail')
else:
    print('Spam Mail')
