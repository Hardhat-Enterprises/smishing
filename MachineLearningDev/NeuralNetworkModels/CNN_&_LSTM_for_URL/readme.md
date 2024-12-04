# CNN + LSTM Model for URL Detection

## Overview

This project is designed to detect phishing and harmful URLs using a hybrid model combining **Convolutional Neural Networks (CNN)** and **Long Short-Term Memory (LSTM)**. The model uses URL sequences along with additional features (such as the presence of special characters and HTTPS) to predict whether a URL is safe or potentially harmful.

## Prerequisites

- **Python 3.x**
- **TensorFlow** and **Keras** for building and training the neural networks
- **Pandas** and **NumPy** for data manipulation
- **Matplotlib** for plotting
- **Scikit-learn** for preprocessing and evaluation

## Dataset Requirements

This model requires a dataset of URLs for training and testing. You can retrieve real-world phishing URLs using the provided script.

- **Dataset Path**: The dataset can be found in the directory:
  - `smishing/MachineLearningDev/Latest_URL_Dataset_for_testing/`
- **Script**: Use the following script to gather real-world phishing URLs:
  - `script_to_gather_real_world_phishing_urls.ipynb`
- **Output File**: The data will be saved as:
  - `Randomized_TestData_Harmful_and_Harmless_URLs.csv`

**Note**: The dataset exceeds GitHub’s 25MB limit, so the file is not included in this git repository. You will need to run the script and retrieve the data locally.

## How to Use the Model

1. **Download the Dataset**: Run the provided script to retrieve and randomize the dataset.
2. **Run the Model**: Place the dataset in the same directory as the model code and ensure that all required dependencies are installed.
3. **Google Colab or Local**: Whether you are using Google Colab or running locally, ensure the dataset is properly loaded in the directory.

For more details on setting up the model, refer to the model documentation inside the code files.
