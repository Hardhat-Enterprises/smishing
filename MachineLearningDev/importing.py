from utils import *
from list_of_functions import *
# Load model directly
from transformers import AutoTokenizer, AutoModelForSequenceClassification

# Not working, something wrong with torch but don't know how to fix
# Import url model from transformers

tokenizer = AutoTokenizer.from_pretrained("kmack/malicious-url-detection")
model = AutoModelForSequenceClassification.from_pretrained("kmack/malicious-url-detection")


for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")
    inputs = tokenizer(message, return_tensors="pt")
    outputs = model(**inputs)

    print_results(outputs)
