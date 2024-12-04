from transformers import BertTokenizer, TFBertForSequenceClassification
import tensorflow as tf
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from imblearn.over_sampling import SMOTE

# Load dataset
df = pd.read_csv('sms_data.csv')
df.rename(columns={'category': 'label'}, inplace=True)
df['label'] = df['label'].map({'ham': 0, 'spam': 1})

# Text Vectorization using TF-IDF for SMOTE
vectorizer = TfidfVectorizer(max_features=5000)
X_tfidf = vectorizer.fit_transform(df['text']).toarray()
y = df['label']

# Apply SMOTE to balance the classes
smote = SMOTE(random_state=42)
X_smote, y_smote = smote.fit_resample(X_tfidf, y)

# Convert back to DataFrame for tokenization
smote_df = pd.DataFrame(X_smote, columns=vectorizer.get_feature_names_out())
smote_df['label'] = y_smote

# Tokenization with BertTokenizer
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
def tokenize_function(text_series):
    return tokenizer(text_series.tolist(), padding='max_length', truncation=True, max_length=128)

# Train-test split
train_texts, test_texts, train_labels, test_labels = train_test_split(df['text'], df['label'], test_size=0.2, random_state=42)

# Tokenize train and test texts
train_encodings = tokenizer(train_texts.tolist(), truncation=True, padding=True, max_length=128)
test_encodings = tokenizer(test_texts.tolist(), truncation=True, padding=True, max_length=128)

# Convert to TensorFlow datasets
train_dataset = tf.data.Dataset.from_tensor_slices((
    dict(train_encodings),
    train_labels
))
test_dataset = tf.data.Dataset.from_tensor_slices((
    dict(test_encodings),
    test_labels
))

# Model
model = TFBertForSequenceClassification.from_pretrained('bert-base-uncased', num_labels=2)

# Compile the model
model.compile(optimizer=tf.keras.optimizers.Adam(learning_rate=5e-5),
              loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              metrics=['accuracy'])

# Train the model
history = model.fit(
    train_dataset.batch(8),
    validation_data=test_dataset.batch(8),
    epochs=3
)

# Save the model and tokenizer
model.save_pretrained('C:/Users/arath/SMISHING/sms_phishing_model')
tokenizer.save_pretrained('C:/Users/arath/SMISHING/tokenizer')

# Evaluate the model
results = model.evaluate(test_dataset.batch(8))
print(f"Test loss: {results[0]}")
print(f"Test accuracy: {results[1]}")
