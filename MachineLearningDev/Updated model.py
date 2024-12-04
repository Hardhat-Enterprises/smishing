#Install packages and librarys

import pandas as pd
import nltk
from nltk.util import ngrams
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import MultinomialNB
from sklearn.metrics import classification_report, accuracy_score
import re
# Ensure NLTK data is downloaded
nltk.download('punkt')


# Read Data set
df = pd.read_csv('spam.csv', encoding='latin1')


#Preprocessing

df = df[['v1', 'v2']]
df.columns = ['label', 'message']
df['label'] = df['label'].map({'ham': 0, 'spam': 1})

# Tokenization function
def tokenize_text(text):
    text = re.sub(r'[^\w\s]', '', text) # Remove all punctuation (,)
    tokens = nltk.word_tokenize(text)
    return tokens
def extract_ngrams(text, n):
    tokens = tokenize_text(text)
    return list(ngrams(tokens, n))

def message_to_ngrams(message, n):
    ngram_list = extract_ngrams(message, n)
    return ' '.join(['_'.join(ngram) for ngram in ngram_list])


# Apply the n-gram extraction
n = 1  # Change value for N-gram type (1 = unigram, 2 = bigram etc...)
df['message_ngrams'] = df['message'].apply(lambda x: message_to_ngrams(x, n))

# Split data into features and labels
X = df['message_ngrams']
y = df['label']


df['message'][0] #plain text


X[0] #Bi-Gram pairs


# Convert text data into numerical data using CountVectorizer
vectorizer = CountVectorizer()
X_vectorized = vectorizer.fit_transform(X)
X_vectorized[0]


# Split data into 80% training 20% testing
X_train, X_test, y_train, y_test = train_test_split(X_vectorized, y, test_size=0.2, random_state=42)


# Train a Naive Bayes classifier
model = MultinomialNB()
model.fit(X_train, y_train)


#Model Evaluation
y_pred = model.predict(X_test)
print("Training Accuracy:", accuracy_score(y_train, model.predict(X_train)))
print("Accuracy:", accuracy_score(y_test, y_pred))
print("Classification Report:")
print(classification_report(y_test, y_pred))


