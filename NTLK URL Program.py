import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, accuracy_score
from sklearn.preprocessing import LabelEncoder


# Load the dataset

dataset_path = 'Randomized_TestData_Harmful_and_Harmless_URLs.csv'
df = pd.read_csv(dataset_path)


# Automatically identify 'label' and 'message' (URL) columns

label_column = None
message_column = None


# Check for common column names

for column in df.columns:
    if column.lower() in ['label', 'labels', 'category']:
        label_column = column
    elif column.lower() in ['message', 'text', 'url']:
        message_column = column


# If the column names are not found, raise an error

if not label_column or not message_column:
    raise ValueError("Could not identify 'label' and 'message' (URL) columns in the dataset")


# Print identified columns

print(f"Using '{label_column}' as the label column and '{message_column}' as the message column.")


# Encode the labels (harmless, malware, etc.) into numeric values

label_encoder = LabelEncoder()
df[label_column] = label_encoder.fit_transform(df[label_column])


# Define a function to map the original labels to the new ones

def map_labels(label):
    original_label = label_encoder.inverse_transform([label])[0]
    if original_label in ['Definitely Phishing', 'Malware', 'Phishing']:
        return 'Harmful'
    elif original_label in ['Likely', 'Suspicious']:
        return 'Potentially Harmful'
    else:
        return 'Harmless'


# Apply the mapping to the label column to create new labels

df['new_label'] = df[label_column].apply(map_labels)


# Encode the new labels

new_label_encoder = LabelEncoder()
df['new_label'] = new_label_encoder.fit_transform(df['new_label'])


# Split the data into train and test sets (80% for testing)

X = df[message_column]
y = df['new_label']
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.8, random_state=42)


# Convert URLs to numerical features using TF-IDF

tfidf_vectorizer = TfidfVectorizer(max_features=1000)  # Adjust max_features as needed
X_train_tfidf = tfidf_vectorizer.fit_transform(X_train)
X_test_tfidf = tfidf_vectorizer.transform(X_test)


# Train a Random Forest Classifier

rf_classifier = RandomForestClassifier(random_state=10)
rf_classifier.fit(X_train_tfidf, y_train)


# Predict on the test set

y_pred = rf_classifier.predict(X_test_tfidf)


# Evaluate the model

print("Accuracy:", accuracy_score(y_test, y_pred))
print("\nClassification Report:")
print(classification_report(y_test, y_pred, target_names=new_label_encoder.classes_))