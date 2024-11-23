from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import accuracy_score, classification_report
import pandas as pd
import re

# Load the dataset
dataset_path = 'Randomized_TestData_Harmful_and_Harmless_URLs.csv'
df = pd.read_csv(dataset_path)

# Automatically identify 'label' and 'message' (URL) columns
label_column = None
url_column = None

# Check for common column names
for column in df.columns:
    if column.lower() in ['label', 'labels', 'category']:
        label_column = column
    elif column.lower() in ['message', 'text', 'url']:
        url_column = column

# If the column names are not found, raise an error
if not label_column or not url_column:
    raise ValueError("Could not identify 'label' and 'message' (URL) columns in the dataset.")

# Print identified columns
print(f"Using '{label_column}' as the label column and '{url_column}' as the URL column.")

# Encode the labels (harmless, malware, etc.) into numeric values
label_encoder = LabelEncoder()
df[label_column] = label_encoder.fit_transform(df[label_column])

# Define a function to map the original labels to new ones
def map_labels(label):
    harmful_labels = [1, 2]  # Update based on dataset (e.g., 1=Harmful, 2=Suspicious)
    return 'Harmful' if label in harmful_labels else 'Harmless'

# Apply the mapping to the label column to create new labels
df['new_label'] = df[label_column].apply(map_labels)

# Encode the new labels
new_label_encoder = LabelEncoder()
df['new_label'] = new_label_encoder.fit_transform(df['new_label'])

# Text Preprocessing Function
def preprocess_url(url):
    url = url.lower()  # Convert to lowercase
    url = re.sub(r'https?://(www\.)?', '', url)  # Remove http:// or https:// and www
    url = re.sub(r'/.*', '', url)  # Keep only the domain
    return url

# Apply text preprocessing
df[url_column] = df[url_column].apply(preprocess_url)

# Advanced Feature Engineering
def extract_features(url):
    return {
        'url_length': len(url),  # Length of the URL
        'special_chars': sum(1 for char in url if char in '@?%&'),  # Count special characters
        'contains_ip': int(bool(re.search(r'\b(?:[0-9]{1,3}\.){3}[0-9]{1,3}\b', url))),  # Check for IP address
        'entropy': -sum((url.count(c) / len(url)) * (url.count(c) / len(url)).bit_length() for c in set(url)),  # Entropy
    }

# Add feature columns to the DataFrame
feature_df = pd.DataFrame(df[url_column].apply(extract_features).tolist())
df = pd.concat([df, feature_df], axis=1)

# Split the data into train and test sets (30% for testing)
X = df[[url_column, 'url_length', 'special_chars', 'contains_ip', 'entropy']]
y = df['new_label']
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

# Prepare a ColumnTransformer for text and numeric features
preprocessor = ColumnTransformer([
    ('tfidf', TfidfVectorizer(max_features=1000), url_column),  # Extract textual features
    ('ngrams', CountVectorizer(ngram_range=(2, 3)), url_column)  # Add n-gram features
], remainder='passthrough')  # Keep numeric features as-is

# Build a pipeline for feature extraction and classification
pipeline = Pipeline([
    ('preprocessor', preprocessor),  # Feature extraction
    ('classifier', RandomForestClassifier(random_state=10))  # Classification
])

# Train the pipeline
pipeline.fit(X_train, y_train)

# Predict on the test set
y_pred = pipeline.predict(X_test)

# Evaluate the model
print("Accuracy:", accuracy_score(y_test, y_pred))
print("\nClassification Report:")
print(classification_report(y_test, y_pred, target_names=new_label_encoder.classes_))
