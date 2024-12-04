import os
import tensorflow as tf
from tensorflow.keras.preprocessing.sequence import pad_sequences
from langdetect import detect
from googletrans import Translator

# Initialize the translator
translator = Translator()

# Load the model and tokenizer for inference
def load_model_and_tokenizer():
    model = tf.keras.models.load_model(os.path.join("C:\\Users\\arath\\MULTILINGUAL AI", 'sms_phishing_model.h5'))
    with open(os.path.join("C:\\Users\\arath\\MULTILINGUAL AI", 'tokenizer.json')) as f:
        tokenizer = tf.keras.preprocessing.text.tokenizer_from_json(f.read())
    return model, tokenizer

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
    # Load the model and tokenizer
    model, tokenizer = load_model_and_tokenizer()

    # Example for testing with a list of sample messages
    test_messages = [
        "¡Has ganado un premio, reclámalo ahora!",
        "Hey, how are you doing?",
        "恭喜你！你赢得了一张免费的机票！点击这里领取。",
        "Félicitations ! Vous avez gagné un bon d'achat de 100 euros. Cliquez ici pour l'obtenir.",
        "이 노래를 들어봐. 너를 생각나게 해"
    ]

    # Predict and print the results for each message
    for msg in test_messages:
        prediction, lang = predict_message(msg, model, tokenizer)
        print(f"Message: {msg} | Prediction: {prediction} | Language Detected: {lang}")
