import pandas as pd
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
import nltk
from nltk.stem import PorterStemmer
from nltk.corpus import stopwords
import warnings
warnings.filterwarnings("ignore")

# Load data
df = pd.read_csv('/Users/gladizgregory2/desktop/dataset.csv')

# Create a dictionary
map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
df['LABEL'] = df['LABEL'].map(map_label)

# Split data into features (X) and labels (y)
X = df['TEXT']
y = df['LABEL']

# Split data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=100)

# Initialize NLTK's Porter Stemmer
stemmer = PorterStemmer()

# Preprocess the stop words list to apply stemming
stop_words = set(stopwords.words('english'))
stop_words_stemmed = set([stemmer.stem(word) for word in stop_words])

# Custom tokenizer function for stemming and handling apostrophes
def stemming_tokenizer(text):
    tokens = nltk.word_tokenize(text)
    stemmed_tokens = [stemmer.stem(token) for token in tokens if token not in stop_words_stemmed and token.isalnum()]
    return stemmed_tokens

# Feature extraction using TF-IDF with stemming and updated stop words
tfidf_vectorizer = TfidfVectorizer(tokenizer=stemming_tokenizer, lowercase=True)
X_train_features = tfidf_vectorizer.fit_transform(X_train)
X_test_features = tfidf_vectorizer.transform(X_test)

# Set up the parameters grid for grid search
param_grid = {
    'n_estimators': [50, 100, 150],
    'max_depth': [None, 10, 20],
    'min_samples_split': [2, 5, 10],
    'min_samples_leaf': [1, 2, 4]
}

# Initialize Random Forest classifier
model = RandomForestClassifier()

# Initialize GridSearchCV
grid_search = GridSearchCV(estimator=model, param_grid=param_grid, cv=5, scoring='accuracy')

# Fit the grid search to the data
grid_search.fit(X_train_features, y_train)

# Get the best parameters and the best score
best_params = grid_search.best_params_
best_score = grid_search.best_score_

print("Best Parameters:", best_params)
print("Best Accuracy:", best_score)

# Train a new model with the best parameters
best_rf_model = RandomForestClassifier(**best_params)
best_rf_model.fit(X_train_features, y_train)

# Evaluate the model
train_accuracy = accuracy_score(y_train, best_rf_model.predict(X_train_features))
test_accuracy = accuracy_score(y_test, best_rf_model.predict(X_test_features))

print('Accuracy on training data: ', train_accuracy)
print('Accuracy on test data: ', test_accuracy)

# Example prediction
input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
input_message_features = tfidf_vectorizer.transform(input_message)
prediction = best_rf_model.predict(input_message_features)

if prediction[0] == 0:
    print('Ham mail')
elif prediction[0] == 1:
    print('Smishing Mail')
else:
    print('Spam Mail')
