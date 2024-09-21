# Phishing and Smishing Detection using BERT and VADER

## Overview

This repository contains the implementation of two models for phishing and smishing detection:
1. BERT: Fine-tuned for classifying messages and emails as either "Safe" or "Phishing".
2. VADER: Used for sentiment analysis to identify emotional manipulation or urgency in messages.

The BERT model has been fine-tuned and saved in the subfolder, ready for use. You can download the required datasets and use the provided Jupyter notebook to either retrain the model or evaluate it on new data.

## How to Run the Code

### 1. Download the Datasets

The original datasets are too large to include in this repository. To run the full implementation, you need to download two datasets:
1. [SMS Spam Collection Dataset](https://www.kaggle.com/datasets/uciml/sms-spam-collection-dataset)
2. [Phishing Email Detection Dataset](https://www.kaggle.com/datasets/elnahas/phishing-email-detection-using-svm-rfc)

Once downloaded, place the dataset files in the same directory as the Jupyter notebook before running the code.

### 2. Using the Pre-trained BERT Model

If you prefer not to retrain the model, you can use the pre-trained BERT model provided in the BERT_Model/ subfolder.

#### Example for Loading the Pre-trained Model:
```python
from transformers import BertTokenizer, BertForSequenceClassification

# Load the model and tokenizer
model = BertForSequenceClassification.from_pretrained('./BERT_Model')
tokenizer = BertTokenizer.from_pretrained('./BERT_Model')

# Use the model for inference on new messages
message = "Your account has been suspended. Click here to verify."
inputs = tokenizer(message, return_tensors='pt', padding=True, truncation=True)
outputs = model(**inputs)
