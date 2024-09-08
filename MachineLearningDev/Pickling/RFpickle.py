#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat May 11 18:39:27 2024

@author: gladizgregory2
"""

import pandas as pd
import pickle
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier

# Load the dataset
df = pd.read_csv('DatasetCombined.csv', encoding='ISO-8859-1')

# Create a dictionary to map labels
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

# Train random forest model
model = RandomForestClassifier()
model.fit(X_train_features, y_train)

# Pickle the model
with open('rf_model_pickle.pkl', 'wb') as f:
    pickle.dump(model, f)

# Pickle the vectorizer
with open('rf_vectorizer_pickle.pkl', 'wb') as f:
    pickle.dump(tfidf_vectorizer, f)

# Load the model
with open('rf_model_pickle.pkl', 'rb') as f:
    loaded_model = pickle.load(f)

# Load the vectorizer
with open('rf_vectorizer_pickle.pkl', 'rb') as f:
    loaded_vectorizer = pickle.load(f)

# User input for the message
input_message = input("Please enter a message: ")

# Use the loaded vectorizer to transform the input message
input_message_features = loaded_vectorizer.transform([input_message])

# Use the loaded model to make a prediction
prediction = loaded_model.predict(input_message_features)

# Print the predicted class
if prediction[0] == 0:
    print('Predicted class: Ham mail')
elif prediction[0] == 1:
    print('Predicted class: Smishing Mail')
else:
    print('Predicted class: Spam Mail')