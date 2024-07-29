#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Apr 29 10:32:22 2024

@author: gladizgregory2
"""

import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.feature_selection import RFE

# Load the dataset 
df = pd.read_csv('/Users/gladizgregory2/desktop/DatasetCombined.csv', encoding='ISO-8859-1')
#df = pd.read_csv('Dataset.csv')

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

# Train logistic regression model
model = RandomForestClassifier()
model.fit(X_train_features, y_train)

# Evaluate the model
train_accuracy = accuracy_score(y_train, model.predict(X_train_features))
test_accuracy = accuracy_score(y_test, model.predict(X_test_features))

print('Accuracy on training data: ', train_accuracy)
print('Accuracy on test data: ', test_accuracy)

# Backward Feature Elimination
selector = RFE(estimator=model, n_features_to_select=10, step=1)
selector = selector.fit(X_train_features, y_train)

# Get selected features
selected_features = selector.support_
X_train_selected = X_train_features[:, selected_features]
X_test_selected = X_test_features[:, selected_features]

# Retrain model on selected features
model.fit(X_train_selected, y_train)

# Evaluate the model on selected features
train_accuracy_selected = accuracy_score(y_train, model.predict(X_train_selected))
test_accuracy_selected = accuracy_score(y_test, model.predict(X_test_selected))

print('Accuracy on training data after feature selection: ', train_accuracy_selected)
print('Accuracy on test data after feature selection: ', test_accuracy_selected)

# Example prediction
input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
input_message_features = tfidf_vectorizer.transform(input_message)
input_message_features_selected = input_message_features[:, selected_features]
prediction = model.predict(input_message_features_selected)

if prediction[0] == 0:
    print('Ham mail')
elif prediction[0] == 1:
    print('Smishing Mail')
else:
    print('Spam Mail')
