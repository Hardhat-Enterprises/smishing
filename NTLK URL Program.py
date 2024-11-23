from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import accuracy_score
import pandas as pd

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
    if label == 1:  # Assuming 1 indicates harmful and 0 indicates harmless
        return 'Harmful'
    else:
        return 'Harmless'

# Apply the mapping to the label column to create new labels
df['new_label'] = df[label_column].apply(map_labels)

# Encode the new labels
new_label_encoder = LabelEncoder()
df['new_label'] = new_label_encoder.fit_transform(df['new_label'])

# Split the data into train and test sets (30% for testing)
X = df[url_column]
y = df['new_label']
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

# Additional ngrams feature to create similarity with other NLTK URL Program for a feature processing pipeline
X_train = X_train.to_frame()  # Reshape for ColumnTransformer compatibility
X_test = X_test.to_frame()

preprocessor = ColumnTransformer([
    ('tfidf', TfidfVectorizer(max_features=1000), url_column),
    ('ngrams', CountVectorizer(ngram_range=(2, 3)), url_column)
])

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
