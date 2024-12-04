from transformers import BertTokenizer, TFBertForSequenceClassification
import os

# Define paths for saving
tokenizer_path = 'C:/Users/arath/SMISHING/tokenizer'
model_path = 'C:/Users/arath/SMISHING/sms_phishing_model'

# Create directories if they do not exist
os.makedirs(tokenizer_path, exist_ok=True)
os.makedirs(model_path, exist_ok=True)

# Initialize the tokenizer and model
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
model = TFBertForSequenceClassification.from_pretrained('bert-base-uncased', num_labels=2)

# Save tokenizer and model locally
tokenizer.save_pretrained(tokenizer_path)
model.save_pretrained(model_path)

print("Model and tokenizer saved successfully.")
print(model)