#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Apr 28 19:12:03 2024

@author: gladizgregory2
"""

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Apr 28 14:25:15 2024

@author: gladizgregory2
"""

import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.decomposition import PCA
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

# Load the dataset
df = pd.read_csv('/Users/gladizgregory2/desktop/DatasetCombined.csv', encoding='ISO-8859-1')

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
X_train_tfidf = tfidf_vectorizer.fit_transform(X_train)
X_test_tfidf = tfidf_vectorizer.transform(X_test)

# Apply dimensionality reduction using PCA
n_components = 100  # You can adjust this number based on your needs
pca = PCA(n_components=n_components)
X_train_features = pca.fit_transform(X_train_tfidf.toarray())
X_test_features = pca.transform(X_test_tfidf.toarray())

# Train RandomForestClassifier model
model = RandomForestClassifier()
model.fit(X_train_features, y_train)

# Evaluate the model
train_accuracy = accuracy_score(y_train, model.predict(X_train_features))
test_accuracy = accuracy_score(y_test, model.predict(X_test_features))

print('Accuracy on training data: ', train_accuracy)
print('Accuracy on test data: ', test_accuracy)

# Example prediction
input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
input_message_tfidf = tfidf_vectorizer.transform(input_message)
input_message_features = pca.transform(input_message_tfidf.toarray())
prediction = model.predict(input_message_features)

if prediction[0] == 0:
    print('Ham mail')
elif prediction[0] == 1:
    print('Smishing Mail')
else:
    print('Spam Mail')
