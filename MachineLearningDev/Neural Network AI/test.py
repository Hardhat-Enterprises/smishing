from transformers import BertTokenizer, TFBertForSequenceClassification
import tensorflow as tf

# Define paths for loading
tokenizer_path = 'C:/Users/arath/SMISHING/tokenizer'
model_path = 'C:/Users/arath/SMISHING/sms_phishing_model'

# Function to load tokenizer and model locally
def load_model_and_tokenizer(tokenizer_path, model_path):
    try:
        tokenizer = BertTokenizer.from_pretrained(tokenizer_path)
        model = TFBertForSequenceClassification.from_pretrained(model_path)
        print("Tokenizer and model loaded successfully!")
        return tokenizer, model
    except Exception as e:
        print(f"An error occurred: {e}")
        raise

# Load tokenizer and model
tokenizer, model = load_model_and_tokenizer(tokenizer_path, model_path)

# Function to prepare and predict on text samples
def predict(texts):
    inputs = tokenizer(texts, return_tensors="tf", padding=True, truncation=True, max_length=128)
    outputs = model(inputs)
    predictions = tf.argmax(outputs.logits, axis=-1)
    return predictions.numpy()

# Test samples
test_samples = [
    "Congratulations! You've won a $1000 gift card. Call now!",
    "Hey, just checking in to see how you're doing.",
    "URGENT! We are trying to contact you. Your account has been compromised. Please verify your identity.",
    "Let's meet for lunch tomorrow. Let me know if you can make it.",
    "Congrats! 1 year special cinema pass for 2 is yours. call 09061209465 now!. Dont miss out! Limited time offer."
]

# Predict and print results
predictions = predict(test_samples)
for text, prediction in zip(test_samples, predictions):
    label = 'spam' if prediction == 1 else 'ham'
    print(f"Text: {text}\nPrediction: {label}\n")
    
