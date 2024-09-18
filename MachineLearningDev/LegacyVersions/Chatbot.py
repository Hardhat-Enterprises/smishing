#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed May  1 18:15:28 2024

@author: gladizgregory2
"""

# Import necessary libraries
import pandas as pd #Import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer #Import TfidfVectorizer from scikit-learn's feature_extraction library
from sklearn.ensemble import RandomForestClassifier #Import RandomForestClassifier from scikit-learn's ensemble library
import re #Import regular expressions library

# Load the dataset and train the model
def train_model():
    df = pd.read_csv('/Users/gladizgregory2/desktop/DatasetCombined.csv', encoding='ISO-8859-1')
    map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
    df['LABEL'] = df['LABEL'].map(map_label)

    X = df['TEXT']
    y = df['LABEL']

    tfidf_vectorizer = TfidfVectorizer(min_df=1, stop_words='english', lowercase=True)
    X_features = tfidf_vectorizer.fit_transform(X)

    model = RandomForestClassifier()
    model.fit(X_features, y)
    
    return model, tfidf_vectorizer

# Predict label for a given message
def predict_label(message, model, tfidf_vectorizer):
    input_message_features = tfidf_vectorizer.transform([message])
    prediction = model.predict(input_message_features)
    return prediction[0]

# Define a function to check if the input message is a general inquiry
def general_enquiry(message):
    # Define keywords indicating general inquiries
    general_inquiry_keywords = ['how', 'what', 'when', 'where', 'who', 'why']
    
    # Check if the message contains any of the keywords
    for keyword in general_inquiry_keywords:
        if re.search(r'\b{}\b'.format(keyword), message.lower()):
            return True
    return False

# Provide advice based on the classification
def provide_response(prediction, message):
    if prediction == 1:
        print("\U0001F6A8 WARNING \U0001F6A8")
        print("This message is classified as potential smishing")
        print("DO NOT CLICK THE LINK.")
    else:
        if general_enquiry(message):
            print("A type of cybercrime that uses deceptive text messages to manipulate victims into disclosing sensitive information")
        else:    
            print("This message is not classified as smishing.")

# Chatbot function
def smishing_chatbot():
    print("Welcome to Smishing Detection Chatbot!")
    model, tfidf_vectorizer = train_model()
    
    while True:
        user_input = input("Enter a message (type 'quit' to exit): ")
        if user_input.lower() == 'quit':
            print("Goodbye! Have a lovely day 😝")
            break
        
        prediction = predict_label(user_input, model, tfidf_vectorizer)
        provide_response(prediction, user_input)

# Run the chatbot
if __name__ == "__main__":
    smishing_chatbot()
