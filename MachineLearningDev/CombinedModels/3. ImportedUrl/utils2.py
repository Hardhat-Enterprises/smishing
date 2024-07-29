import joblib
import pandas as pd
import numpy as np	
import os
import re
from datetime import datetime
import matplotlib.pyplot as plt
import seaborn as sns
from imblearn.over_sampling import SMOTE
from imblearn.under_sampling import RandomUnderSampler
from imblearn.pipeline import Pipeline
from collections import Counter, defaultdict
from sklearn.model_selection import train_test_split, GridSearchCV, RandomizedSearchCV, cross_val_score
from sklearn.ensemble import VotingClassifier
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.datasets import load_digits
from joblib import parallel_backend
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, recall_score, make_scorer
from sklearn.decomposition import PCA, TruncatedSVD, NMF
from scipy.sparse import csr_matrix, hstack, vstack
from matplotlib import colormaps
# Models
from sklearn.neighbors import KNeighborsClassifier 
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
#for gpu acceleration, not working yet
#from cuml.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.ensemble import AdaBoostClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.naive_bayes import MultinomialNB, BernoulliNB

# This version is not in a class, need to put variables in functions and pass them around

#sample_msg = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
# These are some sample messages we found easy to get wrong. 
# Particularly, pizza and DiDi should be spam (maybe ham) not smishing, Chemist Warehouse should be ham not smishing
# The first 3 are smishing which they usually get right
sample_messages = [
    "Hello, you still have a fine that has not been paid. Please pay it in time, otherwise it will affect your travel. https://linkstps.xyz/au",
    "Eastlink: There is an outstanding debt on the toll invoice. Settlement should always be made before the maturity datae. https://tolls.eastlink.click/online",
    "We're unable to deliver your online package due to an address error. Please click promptly to update the address for re-delivery. https://ausorriso.xyz/i",
    "TODAY ONLY 40% off traditional and premium pizzas* ORDER NOW dominoes.au/7MKXrbmdRk T&Cs apply. To opt out send STOP to 0485865365",
    "Chemist Warehouse Fountain Gate sent you a Slyp receipt. View it here: https://reciepts.slyp.com.au/WRA-b94511379edf4de481438e-f4a6e952c0",
    "[DiDi]$15 off next 2 rides!* Savings automatically applied on your next ride request. Until Sunday. https://dd.me/k3M7s23 Opt out: https://dd.me/b6kHCl5",
    "JUST ANNOUNCED Big Apple $750 Scholarship Study in New York State (USA) this July. 4-week program designed for Australian Uni students! www.cisaustalia.com.au"
]

# Select what models to use: comment out the models you don't want to use, no need to change param_grid etc.
models = [
    ("Naive Bayes multinomial", MultinomialNB()),
    ("AdaBoost", AdaBoostClassifier()),
    ("Random Forest", RandomForestClassifier()),
    #("Multi-layer Perceptron", MLPClassifier()), #not working
    ("Naive Bayes multivariate Bernoulli", BernoulliNB()),
    ("Decision Tree", DecisionTreeClassifier()),
    ("KNN", KNeighborsClassifier()), #resource intensive
    ("Logistic Regression", LogisticRegression()),
    #("Support Vector", SVC()) #quite slow
]

param_grid = {
    "Naive Bayes multinomial": {'alpha': [0.1, 1.0, 10.0], 'fit_prior': [True, False]},
    "AdaBoost": {'n_estimators': [50, 100, 200]},
    "Random Forest": {'n_estimators': [50, 100, 150], 'max_depth': [None, 5, 10, 15], 'min_samples_split': [2, 5, 10], 'min_samples_leaf': [1, 2, 4], 'max_features': ['sqrt', 'log2'], 'n_jobs': [-1]},
    "Multi-layer Perceptron": {'max_iter':10000, 'hidden_layer_sizes': [(50,50,50), (50,100,50), (100,)], 'activation': ['tanh', 'relu'], 'solver': ['sgd', 'adam'], 'alpha': [0.0001, 0.05],'learning_rate': ['constant','adaptive'], 'njobs':[-1]},
    "Naive Bayes multivariate Bernoulli": {'alpha': [0.1, 1.0, 10.0], 'fit_prior': [True, False]},
    "Decision Tree": {'max_depth': [None, 10, 20, 50]},
    "KNN": {'n_neighbors': [3, 5, 10], 'weights': ['uniform', 'distance']},
    "Logistic Regression": {'C': [0.1, 1.0, 10.0], 'penalty': ['l2'], 'max_iter': [1000], 'solver': ['newton-cg', 'lbfgs', 'liblinear', 'sag', 'saga']},
    "Support Vector": {'C': [0.1, 1.0, 10.0], 'gamma': [0.1, 1.0, 10.0], 'random_state': [42]}
}

models_info = {}
for name, model in models:
    models_info[name] = {
        'name': name,
        'model': model,
        'param_grid': param_grid[name],
        'best_param': None,  
        'grid_score': None, 
        'cross_val_score': None, 
        'weight': None, 
        # Train Accuracy, Test Accuracy , Precision, Recall, F1
        'evaluation': None
    }


def load_csv(dataset_path='DatasetCombined.csv'):
    df = pd.read_csv(dataset_path, encoding='ISO-8859-1')
    # Create Dictionary
    map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
    df['LABEL'] = df['LABEL'].map(map_label)
    return df

def extract_urls(df):
    # Extract URLs from the text
    df['LINK'] = df['TEXT'].str.extract(r'(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)')
    # Replace NaN values with an empty string
    df['LINK'] = df['LINK'].fillna('none')
    # Keep only rows where 'LINK' is not an empty string
    #df = df[df['LINK'] != '']
    return df['LINK']

def split_dataset(df, column='TEXT', label = 'LABEL'):
    # Split data into features (X) and labels (y)
    X = df[column]
    y = df[label]
    # Split data into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=100)
    return X_train, X_test, y_train, y_test

def feature_extraction(X_train, X_test, X_valid):
    # Feature extraction using TF-IDF
    #tfidf_vectorizer = TfidfVectorizer(min_df=1, stop_words='english', lowercase=True)
    # For url
    tfidf_vectorizer = TfidfVectorizer(token_pattern=r'\b\w+\b', ngram_range=(1, 3), lowercase=False)
    X_train_features = tfidf_vectorizer.fit_transform(X_train)
    X_test_features = tfidf_vectorizer.transform(X_test)
    X_valid_features = tfidf_vectorizer.transform(X_valid)
    return tfidf_vectorizer, X_train_features, X_test_features, X_valid_features
    
def split_text_and_link(df, X_train, X_test):# I think this has problem as vectorizer updated?
    split_dataset(df, "TEXT")
    text_vectorizer , _, _= feature_extraction(X_train, X_test)
    extract_urls(df)
    split_dataset(df, "LINK")
    url_X_train_features, url_X_test_features = feature_extraction()
    return url_X_train_features, url_X_test_features

def merge_url_feature(df, X_train, X_test):
    # This doesn't work yet, because of the number of features difference
    url_X_train_features, url_X_test_features = split_text_and_link(df, X_train, X_test)
    # Check if any URLs are missing
    if X_train.shape[0] > url_X_train_features.shape[0]:
        # Create a placeholder for missing URLs
        placeholder = csr_matrix((X_train.shape[0] - url_X_train_features.shape[0], url_X_train_features.shape[1]))
        # Append the placeholder to the URL features
        url_X_train_features = vstack([url_X_train_features, placeholder])
    # Do the same for the test features
    if X_test.shape[0] > url_X_test_features.shape[0]:
        placeholder = csr_matrix((X_test.shape[0] - url_X_test_features.shape[0], url_X_test_features.shape[1]))
        url_X_test_features = vstack([url_X_test_features, placeholder])
    X_train_features = hstack([text_X_train_features, url_X_train_features])
    X_test_features = hstack([text_X_test_features, url_X_test_features])
    return X_train_features, X_test_features

# testing to increase speed
def dimensionality_reduction(X_train_features, X_test_features, method='nmf'):
    if method == 'nmf':
        reduce = NMF(n_components=100)
    elif method == 'pca':
        reduce = pca = PCA(n_components=2)
    X_train_features = reduce.fit_transform(X_train_features)
    X_test_features = reduce.transform(X_test_features)

    
def predict_dim_reduce(feature_input):
    # This doesn't work yet, because of the number of features difference
    reduced = nmf.transform(feature_input)
    return reduced
        
def balance_data(y_train, X_train_features):
    # Count class distribution
    print("Class distribution before balancing:", Counter(y_train))

    # Define resampling strategies for minority classes
    over_sampler = SMOTE(sampling_strategy='auto', random_state=42)
    under_sampler = RandomUnderSampler(sampling_strategy='auto', random_state=42)

    # Define resampling pipeline
    resampling_pipeline = Pipeline([
        ('over_sampling', over_sampler),
        ('under_sampling', under_sampler)
    ])

    # Apply resampling to balance classes
    X_train_resampled, y_train_resampled = resampling_pipeline.fit_resample(X_train_features, y_train)

    # Count class distribution after balancing
    print("Class distribution after balancing:", Counter(y_train_resampled))
    # Update as X train features and y train
    X_train_features, y_train = X_train_resampled, y_train_resampled
    return X_train_features, y_train


# Hyperparameter tuning
def param_tuning(name, model, X_train_features, y_train):
    num_folds = 5
    best_scores = defaultdict(float)
    param_grid = models_info[name]['param_grid']
    #if name == "Multi-layer Perceptron":
        # Use RandomizedSearchCV for Multi-layer Perceptron since it's way too slow
        #grid_search = RandomizedSearchCV(model, param_distributions=param_grid, cv=num_folds, return_train_score=True)
    #elif name == "Random Forest":
    #    with parallel_backend('multiprocessing'):
    #        grid_search = GridSearchCV(RandomForestClassifier(), param_grid, cv=5)
    #else:
        # Use GridSearchCV for other models
    with parallel_backend('threading'):
        grid_search = GridSearchCV(model, param_grid, cv=num_folds, return_train_score=True)
        grid_search.fit(X_train_features, y_train)
    # Store the best score and best parameters for each model
    models_info[name]['best_param'] = grid_search.best_params_
    models_info[name]['grid_score'] = grid_search.best_score_
    print(f"Best parameters for {name}: {models_info[name]['best_param']}")
    print(f"Best grid search score for {name}: {models_info[name]['grid_score']}")


def train_model(name, model, X_train_features, y_train):
    best_parameters = models_info[name]['best_param']
    model.set_params(**best_parameters)
    model.fit(X_train_features, y_train)
    print(f"{name} trained with best parameters!")
    return model


# Evaluate the model
def evaluate_model(name, model, X_train_features, X_test_features, y_train, y_test):
    train_accuracy = accuracy_score(y_train, model.predict(X_train_features))
    test_accuracy = accuracy_score(y_test, model.predict(X_test_features))
    precision = precision_score(y_test, model.predict(X_test_features), average='weighted')
    recall = recall_score(y_test, model.predict(X_test_features), average='weighted')
    f1 = f1_score(y_test, model.predict(X_test_features), average='weighted')
    models_info[name]['evaluation']=[train_accuracy, test_accuracy, precision, recall, f1]
    print('Accuracy on training data: '.ljust(30), train_accuracy)
    print('Accuracy on test data: '.ljust(30), test_accuracy)
    print('Precision: '.ljust(30), precision)
    print('Recall: '.ljust(30), recall)
    print('F1: '.ljust(30), f1)
    return train_accuracy, test_accuracy, precision, recall, f1

def visualise_data():
    n = len(models_info)
    m = len(next(iter(models_info.values()))['evaluation'])
    width = 0.1
    x = np.arange(n)
    score_names = ['train_accuracy', 'test_accuracy', 'precision', 'recall', 'f1']
    fig, ax = plt.subplots(figsize=(10, 5))
    for i in range(m):
        scores = [models_info[name]['evaluation'][i] for name in models_info]
        bars = ax.bar(x + i * width, scores, width, label=score_names[i])
        for bar in bars:
            yval = bar.get_height()
            ax.text(bar.get_x() + bar.get_width()/2, yval + 0.01, round(yval, 3), ha='center', va='bottom', fontsize=5, rotation=45)
    ax.set_xlabel('Models')
    ax.set_ylabel('Scores')
    ax.set_title(f'{os.path.basename(__file__)} Model comparison')
    ax.set_xticks(x)
    ax.set_xticklabels([name for name in models_info])
    ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),
          fancybox=True, shadow=True, ncol=5)
    fig.tight_layout()
    plt.show()


    
# For model list
def cross_validation(X_features, y):
    recall_scorer = make_scorer(recall_score, average=None, labels=[1])
    for name, model in models:
        scores = cross_val_score(model, X_features, y, cv=5, scoring=recall_scorer)
        models_info[name]['cross_val_score'] = scores.mean()
        print(f"Cross-validation scores for {name}: ".ljust(65), models_info[name]['cross_val_score'])
    total_score = sum(model['cross_val_score'] for model in models_info.values())
    for name, model in models_info.items():
        model['weight'] = model['cross_val_score'] / total_score
        print(f"Weight for {name}:".ljust(50), model['weight'])

# For voting model
def train_voting_model(X_train_features, y_train):
    weights = [models_info[name]['weight'] for name, _ in models]
    votingClassifier = VotingClassifier(estimators=models, voting='soft', weights=weights, verbose=True)
    votingClassifier.fit(X_train_features, y_train)
    models_info['voting'] = {
        'name': 'Voting',
        'model': votingClassifier,
        'param_grid': None,
        'best_param': None,
        'grid_score': None,
        'cross_val_score': None,
        'weight': None,
        'evaluation': None
    }
    return votingClassifier

def split_predict(tfidf_vectorizer): #not working yet, num. of feature don't match
    input_url = ' '.join(re.findall(r'(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)', input_message))
    url_features = tfidf_vectorizer.transform([input_url])
    # If the URL is missing, create a placeholder
    if url_features.shape[0] == 0:
        placeholder = csr_matrix((1, url_X_train_features.shape[1]))
        url_features = placeholder
    # Merge the text and URL features
    input_message_features = hstack([text_features, url_features])

    # PCA optional
    #input_message_features = pca_reduce(input_message_features)

# Make prediction
def make_predict(model, input_message, tfidf_vectorizer, reduce=False):
    text_features = tfidf_vectorizer.transform([input_message])
    #if reduce:
        #predict_dim_reduce(text_features)
    #split_predict()
    #if input_message_features != None:
        #features = input_message_features
    #else:
        #features = text_features
    prediction = model.predict(text_features)
    #confidence = model.predict_proba(input_message_features)[:, 1]
    return prediction
def print_predict(prediction):
    print(prediction)


# Format result    
def get_result(prediction):
    if prediction[0] == 0:
        result = 'Ham mail'
    elif prediction[0] == 1:
        result = 'Smishing Mail'
    else:
        result = 'Spam Mail'
    return result

def keep_record(name, run_time, comments=None):
    # Define the columns of your log DataFrame
    columns = ['run_time', 'model', 'train_accuracy', 'test_accuracy', 'precision', 'recall', 'f1_score',  'cv_score', 'best_param', 'comments']
    # Check if the log file already exists
    if os.path.exists('test_log.csv'):
        # If it exists, load it into a DataFrame
        rec = pd.read_csv('test_log.csv')
    else:
        # If it doesn't exist, create a new DataFrame
        rec = pd.DataFrame(columns=columns)
    # Append a new row to the DataFrame
    if 'best_param' in models_info[name]:
        best_param = models_info[name]['best_param']
    else:
        best_param = None
    if 'cross_val_score' in models_info[name]:
        cross_val_score = models_info[name]['cross_val_score']
    else:
        cross_val_score = None

    model_info = models_info[name]
    evaluation_metrics = model_info['evaluation']
    # The evaluation metrics are stored in a list in the order [train_accuracy, test_accuracy, precision, recall, f1]
    train_accuracy = evaluation_metrics[0]
    test_accuracy = evaluation_metrics[1]
    precision = evaluation_metrics[2]
    recall = evaluation_metrics[3]
    f1 = evaluation_metrics[4]
    record = [run_time, name, train_accuracy, test_accuracy, precision, recall, f1, cross_val_score, best_param, comments] 
    rec.loc[len(rec.index)] = record
    # Write the DataFrame to a CSV file
    rec.to_csv('test_log.csv', index=False)



# Print result
def print_results(prediction):
    if prediction[0] == 0:
        print('Ham mail')
    elif prediction[0] == 1:
        print('Smishing Mail')
    else:
        print('Spam Mail')

# Aggregate predictions
def hard_voting(all_predictions):
    print("Let's vote! ")
    # Count occurrences of each prediction
    counts = {0: 0, 1: 0, 2: 0}
    for i, (name, prediction) in enumerate(all_predictions):
        print(f"{name}: {prediction}")

    for name, prediction in all_predictions:
        counts[prediction] += 1

    # Determine the most common prediction
    most_common_prediction = max(counts, key=counts.get)

    # Output the most common prediction
    if most_common_prediction == 0:
        print('Final Decision: Ham mail\n')
    elif most_common_prediction == 1:
        print('Final Decision: Smishing Mail\n')
    else:
        print('Final Decision: Spam Mail\n')




