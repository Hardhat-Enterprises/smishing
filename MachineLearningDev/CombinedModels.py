#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Apr 15 21:28:03 2024

@author: gladizgregory2
"""

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Apr 15 21:05:47 2024

@author: gladizgregory2
"""

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Apr 15 21:02:04 2024

@author: gladizgregory2
"""

import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
from sklearn.tree import DecisionTreeClassifier
from sklearn.naive_bayes import MultinomialNB
from sklearn.metrics import accuracy_score

# Load the dataset
df = pd.read_csv('DatasetCombined.csv', encoding='ISO-8859-1')

# Create a dictionary for labels
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

# Initialize models
models = {
    'Random Forest': RandomForestClassifier(),
    'KNN': KNeighborsClassifier(),
    'SVM': SVC(),
    'Decision Tree': DecisionTreeClassifier(),
    'Naive Bayes': MultinomialNB()
}

# Train and evaluate each model
for name, model in models.items():
    model.fit(X_train_features, y_train)
    train_accuracy = accuracy_score(y_train, model.predict(X_train_features))
    test_accuracy = accuracy_score(y_test, model.predict(X_test_features))
    print(f'{name} - Accuracy on training data: {train_accuracy:.4f}, Accuracy on test data: {test_accuracy:.4f}')

# Example predictions
sample_messages = [
    "Hello, you still have a fine that has not been paid. Please pay it in time, otherwise it will affect your travel. https://linkstps.xyz/au",
    "Eastlink: There is an outstanding debt on the toll invoice. Settlement should always be made before the maturity datae. https://tolls.eastlink.click/online",
    "We're unable to deliver your online package due to an address error. Please click promptly to update the address for re-delivery. https://ausorriso.xyz/i",
    "TODAY ONLY 40% off traditional and premium pizzas* ORDER NOW dominoes.au/7MKXrbmdRk T&Cs apply. To opt out send STOP to 0485865365",
    "Chemist Warehouse Fountain Gate sent you a Slyp receipt. View it here: https://reciepts.slyp.com.au/WRA-b94511379edf4de481438e-f4a6e952c0",
]

# Transform sample messages using TF-IDF vectorizer
sample_message_features = tfidf_vectorizer.transform(sample_messages)

# Predict labels for sample messages using model
for name, model in models.items():
    predictions = model.predict(sample_message_features)
    print(f'\n{name} predictions:')
    for message, prediction in zip(sample_messages, predictions):
        label = 'Spam' if prediction == 2 else 'Smishing' if prediction == 1 else 'Ham'
        print(f'Message: {message} - Prediction: {label}')
