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


# Load and preprocess the training dataset

df_train = pd.read_csv('DatasetCombined.csv', encoding='latin1')


# Automatically select and rename the first columns

df_train.columns = ['label', 'text', 'url', 'email', 'phone']


# Map label values to numerical format ('ham' -> 0, 'spam' -> 1, 'smishing' -> 2)

df_train['label'] = df_train['label'].map({'ham': 0, 'spam': 1, 'smishing': 2})
df_train = df_train.dropna(subset=['label'])  # Ensure no missing labels


# Replace NaN values in text, url, email, and phone columns with empty strings

df_train['text'] = df_train['text'].fillna('')
df_train['url'] = df_train['url'].fillna('')
df_train['email'] = df_train['email'].fillna('')
df_train['phone'] = df_train['phone'].fillna('')


# Concatenate text, URL, email, and phone columns

df_train['combined_message'] = df_train['text'] + ' ' + df_train['url'] + ' ' + df_train['email'] + ' ' + df_train['phone']


# Tokenization function

def tokenize_text(text):
    # Convert to string in case of any non-string values
    text = str(text)
    text = re.sub(r'[^\w\s]', '', text)  # Remove all punctuation
    tokens = nltk.word_tokenize(text)
    return tokens

def extract_ngrams(text, n):
    tokens = tokenize_text(text)
    return list(ngrams(tokens, n))

def message_to_ngrams(message, n):
    ngram_list = extract_ngrams(message, n)
    return ' '.join(['_'.join(ngram) for ngram in ngram_list])


# Apply the n-gram extraction to the combined training data

n = 3  # Change value for N-gram type (1 = unigram, 2 = bigram, etc.)
df_train['message_ngrams'] = df_train['combined_message'].apply(lambda x: message_to_ngrams(x, n))


# Split training data into features and labels

X_train = df_train['message_ngrams']
y_train = df_train['label']


# Convert text data into numerical data using CountVectorizer

vectorizer = CountVectorizer()
X_train_vectorized = vectorizer.fit_transform(X_train)


# Train the Naive Bayes classifier

model = MultinomialNB()
model.fit(X_train_vectorized, y_train)


# Load and preprocess a new dataset

df_new = pd.read_csv('Dataset_5971.csv', encoding='latin1') #Change file for data that the user wants to test


# Automatically select and rename the first columns

df_new.columns = ['label', 'text', 'url', 'email', 'phone']


# Handle missing values in the label column

df_new['label'] = df_new['label'].map({'ham': 0, 'spam': 1, 'smishing': 2})
df_new = df_new.dropna(subset=['label'])  # Remove rows where 'label' is NaN


# Replace NaN values in text, url, email, and phone columns with empty strings

df_new['text'] = df_new['text'].fillna('')
df_new['url'] = df_new['url'].fillna('')
df_new['email'] = df_new['email'].fillna('')
df_new['phone'] = df_new['phone'].fillna('')


# Concatenate text, URL, email, and phone columns in the new dataset

df_new['combined_message'] = df_new['text'] + ' ' + df_new['url'] + ' ' + df_new['email'] + ' ' + df_new['phone']


# Apply the n-gram extraction to the new data

df_new['message_ngrams'] = df_new['combined_message'].apply(lambda x: message_to_ngrams(x, n))


# Convert the new data into the same numerical format

X_new_vectorized = vectorizer.transform(df_new['message_ngrams'])


# Predict the labels for the new dataset

new_predictions = model.predict(X_new_vectorized)


# True labels for evaluation

Y_new = df_new['label']


# Generate and print the classification report for new data with labels in the desired order

label_mapping = {0: 'ham', 1: 'spam', 2: 'smishing'}
target_names = ['ham', 'spam', 'smishing']  # Define the order of labels

print("Classification Report for New Data:")
print(classification_report(Y_new, new_predictions, target_names=target_names))
