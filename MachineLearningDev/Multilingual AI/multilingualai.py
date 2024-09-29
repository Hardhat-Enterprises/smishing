import os
import tensorflow as tf
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, LSTM, Dense, Dropout
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from langdetect import detect
from googletrans import Translator
import pandas as pd

# Initialize the translator
translator = Translator()

# Load and preprocess the SMS dataset
def load_sms_dataset(filepath):
    # Load the dataset (CSV file)
    data = pd.read_csv(filepath)
    texts = data['text'].tolist()  # Adjust the column name as needed
    labels = data['label'].tolist()  # Adjust the column name as needed
    return texts, labels

# Prepare data for training
def prepare_data(texts, labels):
    # Encode labels to integers (ham=0, spam=1)
    label_encoder = LabelEncoder()
    labels_encoded = label_encoder.fit_transform(labels)

    # Tokenize and pad the input texts
    tokenizer = Tokenizer(num_words=10000, oov_token='<OOV>')
    tokenizer.fit_on_texts(texts)
    sequences = tokenizer.texts_to_sequences(texts)
    padded_sequences = pad_sequences(sequences, maxlen=50, padding='post')

    return padded_sequences, labels_encoded, tokenizer

# Create the LSTM-based model for text classification
def create_model(vocab_size):
    model = Sequential([
        Embedding(vocab_size, 128, input_length=50),
        LSTM(128, return_sequences=True),
        Dropout(0.2),
        LSTM(64),
        Dense(32, activation='relu'),
        Dense(1, activation='sigmoid')
    ])
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
    return model

# Train the model and save it
def train_and_save_model():
    # Load the dataset from the specified path
    dataset_path = os.path.join("C:\\Users\\arath\\MULTILINGUAL AI", "sms_data.csv")  # Ensure this filename matches your dataset
    texts, labels = load_sms_dataset(dataset_path)

    # Prepare training data
    X, y, tokenizer = prepare_data(texts, labels)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    # Create and train the model
    model = create_model(vocab_size=10000)
    model.fit(X_train, y_train, epochs=5, batch_size=32, validation_data=(X_test, y_test))

    # Save the model and tokenizer
    model.save(os.path.join("C:\\Users\\arath\\MULTILINGUAL AI", 'sms_phishing_model.h5'))
    with open(os.path.join("C:\\Users\\arath\\MULTILINGUAL AI", 'tokenizer.json'), 'w') as f:
        f.write(tokenizer.to_json())

# Detect language of a given text
def detect_language(text):
    return detect(text)

# Translate text to English
def translate_to_english(text, src_lang):
    try:
        translated = translator.translate(text, src=src_lang, dest='en')
        return translated.text
    except Exception as e:
        print(f"Error in translation: {e}")
        return text  # If translation fails, return original

# Load the model for inference
def load_model_and_tokenizer():
    model = tf.keras.models.load_model(os.path.join("C:\\Users\\arath\\MULTILINGUAL AI", 'sms_phishing_model.h5'))
    with open(os.path.join("C:\\Users\\arath\\MULTILINGUAL AI", 'tokenizer.json')) as f:
        tokenizer = tf.keras.preprocessing.text.tokenizer_from_json(f.read())
    return model, tokenizer

# Predict if a message is phishing (spam) or not, after translation
def predict_message(message, model, tokenizer):
    # Detect the language of the message
    lang = detect_language(message)

    # Translate to English if not already in English
    if lang != 'en':
        message = translate_to_english(message, lang)

    # Preprocess the message for the model
    sequence = tokenizer.texts_to_sequences([message])
    padded_sequence = pad_sequences(sequence, maxlen=50, padding='post')

    # Predict the class
    prediction = model.predict(padded_sequence)[0][0]
    return 'spam' if prediction > 0.5 else 'ham', lang

if __name__ == '__main__':
    # Train and save the model
    train_and_save_model()

    # Example for testing with a sample message
    test_message = "¡Has ganado un premio, reclámalo ahora!"
    
    # Load the model and tokenizer
    model, tokenizer = load_model_and_tokenizer()
    
    # Predict and print the result
    prediction, lang = predict_message(test_message, model, tokenizer)
    print(f"Prediction: {prediction} | Language Detected: {lang}")
