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
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, recall_score, make_scorer, roc_auc_score, confusion_matrix, balanced_accuracy_score
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


#sample_msg = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
sample_messages = [
    "Hello, you still have a fine that has not been paid. Please pay it in time, otherwise it will affect your travel. https://linkstps.xyz/au",
    "Eastlink: There is an outstanding debt on the toll invoice. Settlement should always be made before the maturity datae. https://tolls.eastlink.click/online",
    "We're unable to deliver your online package due to an address error. Please click promptly to update the address for re-delivery. https://ausorriso.xyz/i",
    "TODAY ONLY 40% off traditional and premium pizzas* ORDER NOW dominoes.au/7MKXrbmdRk T&Cs apply. To opt out send STOP to 0485865365",
    "Chemist Warehouse Fountain Gate sent you a Slyp receipt. View it here: https://reciepts.slyp.com.au/WRA-b94511379edf4de481438e-f4a6e952c0",
    "[DiDi]$15 off next 2 rides!* Savings automatically applied on your next ride request. Until Sunday. https://dd.me/k3M7s23 Opt out: https://dd.me/b6kHCl5",
    "JUST ANNOUNCED Big Apple $750 Scholarship Study in New York State (USA) this July. 4-week program designed for Australian Uni students! www.cisaustalia.com.au"
]

# Select what models to use: comment out the ones you don't want to use, no need to change the param_grid etc.
models = [
    ("Naive Bayes multinomial", MultinomialNB()), #naive bayes ones are quick
    ("AdaBoost", AdaBoostClassifier()),
    ("Random Forest", RandomForestClassifier()), #good accurcies
    #("Multi-layer Perceptron", MLPClassifier()), #not working
    ("Naive Bayes multivariate Bernoulli", BernoulliNB()),
    ("Decision Tree", DecisionTreeClassifier()),
    ("KNN", KNeighborsClassifier()), #resource intensive
    ("Logistic Regression", LogisticRegression()),
    #("Support Vector", SVC()) #quite slow not tested much
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
    "Support Vector": {'probability':[True],'C': [0.1, 1.0, 10.0], 'gamma': [0.1, 1.0, 10.0], 'random_state': [42]}
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
        'roc_auc': None, 
        'balanced accuracy': None,
        'weight': None, 
        # Train Accuracy, Test Accuracy, Balanced Accuracy , Precision, Recall, F1
        'evaluation': None
    }



class ModelPipeline:
    def __init__(self):
        self.tfidf_vectorizer = None
        self.X_train = None
        self.X_test = None
        self.y_train = None
        self.y_test = None
        self.X_train_features = None
        self.X_test_features = None
        self.input_message = None
        self.input_message_features = None
        self.prediction = None
        self.model = None


    def load_dataset(self, dataset_path='DatasetCombined.csv'):
        self.df = pd.read_csv(dataset_path, encoding='ISO-8859-1')
        # Create Dictionary
        map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
        self.df['LABEL'] = self.df['LABEL'].map(map_label)
        return self.df
    
    def missing_ratio(self):
        df = self.df
        # Check for missing values and calculate the missing value ratio
        missing_ratio = df.isnull().sum() / len(df)
        threshold = 0.5  # Set your threshold here
        # Filter out columns with missing value ratio above the threshold
        columns_to_drop = missing_ratio[missing_ratio > threshold].index
        df.drop(columns=columns_to_drop, inplace=True)
        self.df = df
        return self.df

    def extract_urls(self):
        # Extract URLs from the text
        self.df['LINK'] = self.df['TEXT'].str.extract(r'(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)')
        # Replace NaN values with an empty string
        self.df['LINK'] = self.df['LINK'].fillna('none')
        # Keep only rows where 'LINK' is not an empty string
        #self.df = self.df[self.df['LINK'] != '']
        return self.df['LINK']
    
    def split_dataset(self, column='TEXT'):
        # Split data into features (X) and labels (y)
        X = self.df[column]
        y = self.df['LABEL']
        if column == 'LINK':
            self.df = self.df.dropna(subset=['LINK'])
        # Split data into training and testing sets
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(X, y, test_size=0.2, random_state=100)
        # Split data into training set and temporary set
        self.X_train, X_temp, self.y_train, y_temp = train_test_split(X, y, test_size=0.3, random_state=100)
        # Split temporary set into testing set and validation set
        self.X_test, self.X_valid, self.y_test, self.y_valid = train_test_split(X_temp, y_temp, test_size=0.5, random_state=100)
        return self.X_train, self.X_test, self.y_train, self.y_test, self.X_valid, self.y_valid
    
    def feature_extraction(self, url=False):
        # Feature extraction using TF-IDF
        self.tfidf_vectorizer = None
        if url:
            # For url
            self.tfidf_vectorizer = TfidfVectorizer(token_pattern=r'\b\w+\b', ngram_range=(1, 3), lowercase=False)
        else:
            self.tfidf_vectorizer = TfidfVectorizer(min_df=1, stop_words='english', lowercase=True)
        self.X_train_features = self.tfidf_vectorizer.fit_transform(self.X_train)
        self.X_test_features = self.tfidf_vectorizer.transform(self.X_test)
        self.X_valid_features = self.tfidf_vectorizer.transform(self.X_valid)
        return self.tfidf_vectorizer
        
    def split_text_and_link(self):# I think this has problem as vectorizer updated?
        self.split_dataset("TEXT")
        self.text_X_train_features, self.text_X_test_features 
        self.text_vectorizer= self.feature_extraction()
        self.extract_urls()
        self.split_dataset("LINK")
        self.url_X_train_features, self.url_X_test_features = self.feature_extraction()

    def merge_url_feature(self):
        self.split_text_and_link()
        # Check if any URLs are missing
        if self.X_train.shape[0] > self.url_X_train_features.shape[0]:
            # Create a placeholder for missing URLs
            placeholder = csr_matrix((self.X_train.shape[0] - self.url_X_train_features.shape[0], self.url_X_train_features.shape[1]))
            # Append the placeholder to the URL features
            self.url_X_train_features = vstack([self.url_X_train_features, placeholder])
        # Do the same for the test features
        if self.X_test.shape[0] > self.url_X_test_features.shape[0]:
            placeholder = csr_matrix((self.X_test.shape[0] - self.url_X_test_features.shape[0], self.url_X_test_features.shape[1]))
            self.url_X_test_features = vstack([self.url_X_test_features, placeholder])
        self.X_train_features = hstack([self.text_X_train_features, self.url_X_train_features])
        self.X_test_features = hstack([self.text_X_test_features, self.url_X_test_features])
        return self.X_train_features, self.X_test_features
    
    # testing to increase speed
    def dimensionality_reduction(self, method='nmf'):
        if method == 'nmf':
            self.reduce = NMF(n_components=100)
        elif method == 'pca':
            self.reduce = self.pca = PCA(n_components=2)
        self.X_train_features = self.reduce.fit_transform(self.X_train_features)
        self.X_test_features = self.reduce.transform(self.X_test_features)

        
    def predict_dim_reduce(self, feature_input):
        reduced = self.nmf.transform(feature_input)
        return reduced
            
    def balance_data(self):
        # Count class distribution
        print("Class distribution before balancing:", Counter(self.y_train))

        # Define resampling strategies for minority classes
        over_sampler = SMOTE(sampling_strategy='auto', random_state=42)
        under_sampler = RandomUnderSampler(sampling_strategy='auto', random_state=42)

        # Define resampling pipeline
        resampling_pipeline = Pipeline([
            ('over_sampling', over_sampler),
            ('under_sampling', under_sampler)
        ])

        # Apply resampling to balance classes
        self.X_train_resampled, self.y_train_resampled = resampling_pipeline.fit_resample(self.X_train_features, self.y_train)

        # Count class distribution after balancing
        print("Class distribution after balancing:", Counter(self.y_train_resampled))
        # Update as X train features and y train
        self.X_train_features, self.y_train = self.X_train_resampled, self.y_train_resampled
        return self.X_train_features, self.y_train


# Hyperparameter tuning
    def param_tuning(self, name, model):
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
            grid_search.fit(self.X_train_features, self.y_train)
        # Store the best score and best parameters for each model
        models_info[name]['best_param'] = grid_search.best_params_
        models_info[name]['grid_score'] = grid_search.best_score_
        print(f"Best parameters for {name}: {models_info[name]['best_param']}")
        print(f"Best grid search score for {name}: {models_info[name]['grid_score']}")


    def train_model(self, name, model):
        best_parameters = models_info[name]['best_param']
        model.set_params(**best_parameters)
        model.fit(self.X_train_features, self.y_train)
        print(f"{name} trained with best parameters!")
        return model

    
    # Evaluate the model
    def evaluate_model(self, name, model):
        self.y_pred = model.predict(self.X_test_features)
        thresholds = np.arange(0.0, 1.1, 0.1)
        accuracies = []
        precisions = []
        recalls = []
        f1s = []
        try:
            y_pred_proba = model.predict_proba(self.X_test_features)
        except AttributeError:
            raise ValueError(f"The model {name} does not support probability predictions.")
        # Ensure y_pred_proba is a 2D array
        if y_pred_proba.ndim == 1:
            y_pred_proba = y_pred_proba.reshape(-1, 1)
            self.roc_auc = roc_auc_score(self.y_test, y_pred_proba, multi_class='ovr')
        else: 
            self.roc_auc = None

        for threshold in thresholds:
            y_pred = np.argmax(y_pred_proba, axis=1)
            accuracies.append(accuracy_score(self.y_test, y_pred))
            precisions.append(precision_score(self.y_test, y_pred, average='weighted'))
            recalls.append(recall_score(self.y_test, y_pred, average='weighted'))
            f1s.append(f1_score(self.y_test, y_pred, average='weighted'))
        # Plot the metrics by threshold
        plt.figure(figsize=(10, 6))
        plt.plot(thresholds, accuracies, label='Accuracy')
        plt.plot(thresholds, precisions, label='Precision')
        plt.plot(thresholds, recalls, label='Recall')
        plt.plot(thresholds, f1s, label='F1 Score')
        plt.xlabel('Threshold')
        plt.ylabel('Score')
        plt.title('Evaluation Metrics by Threshold')
        plt.legend()
        plt.grid(True)
        plt.show()
        self.train_accuracy = accuracy_score(self.y_train, model.predict(self.X_train_features))
        self.test_accuracy = accuracy_score(self.y_test, self.y_pred)
        self.balanced_accuracy = balanced_accuracy_score(self.y_test, y_pred)
        self.precision = precision_score(self.y_test, self.y_pred, average='weighted')
        self.recall = recall_score(self.y_test, self.y_pred, average='weighted')
        self.f1 = f1_score(self.y_test, self.y_pred, average='weighted')
        
        models_info[name]['evaluation']=[self.train_accuracy, self.test_accuracy, self.balanced_accuracy, self.precision, self.recall, self.f1]
        print('Accuracy on training data: '.ljust(30), self.train_accuracy)
        print('Accuracy on test data: '.ljust(30), self.test_accuracy)
        print('Balanced accuracy: '.ljust(30), self.balanced_accuracy)
        print('Precision: '.ljust(30), self.precision)
        print('Recall: '.ljust(30), self.recall)
        print('F1: '.ljust(30), self.f1)
        if self.roc_auc != None: 
            print('roc_auc: '.ljust(30), self.roc_auc)
        # Visualize confusion matrix
        plt.figure(figsize=(10, 7))
        conf_matrix = confusion_matrix(self.y_test, y_pred)
        sns.heatmap(conf_matrix, annot=True, fmt='d', cmap='Blues')
        plt.xlabel('Predicted')
        plt.ylabel('Actual')
        plt.title(f'Confusion Matrix for {name}')
        plt.show()

    def visualise_data(self, file_name):

        n = len(models_info)
        m = len(next(iter(models_info.values()))['evaluation'])
        width = 0.1
        x = np.arange(n)
        score_names = ['train_accuracy', 'test_accuracy', 'balanced_accuracy', 'precision', 'recall', 'f1', 'roc_auc', 'balanced accuracy']
        fig, ax = plt.subplots(figsize=(10, 5))
        for i in range(m):
            scores = [models_info[name]['evaluation'][i] for name in models_info]
            bars = ax.bar(x + i * width, scores, width, label=score_names[i])
            for bar in bars:
                yval = bar.get_height()
                ax.text(bar.get_x() + bar.get_width()/2, yval + 0.01, round(yval, 3), ha='center', va='bottom', fontsize=5, rotation=45)
        ax.set_xlabel('Models')
        ax.set_ylabel('Scores')
        ax.set_title(f'{file_name}Model comparison')
        ax.set_xticks(x)
        ax.set_xticklabels([name for name in models_info])
        ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),
          fancybox=True, shadow=True, ncol=5)
        fig.tight_layout()
        plt.show()


        
# For model list
## Key decision here: scoring system to use: recall (i.e. out of all positives how much is it able to predict) default is 'accuracy'
    def cross_validation(self):
        recall_scorer = make_scorer(recall_score, average=None, labels=[1])
        for name, model in models:
            scores = cross_val_score(model, self.X_train_features, self.y_train, cv=5, scoring=recall_scorer, n_jobs=-1)
            models_info[name]['cross_val_score'] = scores.mean()
            print(f"Cross-validation scores for {name}: ".ljust(65), models_info[name]['cross_val_score'])
        total_score = sum(model['cross_val_score'] for model in models_info.values())
        for name, model in models_info.items():
            model['weight'] = model['cross_val_score'] / total_score
            print(f"Weight for {name}:".ljust(50), model['weight'])

# For voting model
## Key decision here: voting system to use: soft voting.
    def train_voting_model(self):
        weights = [models_info[name]['weight'] for name, _ in models]
        self.votingClassifier = VotingClassifier(estimators=models, voting='soft', weights=weights, verbose=True, n_jobs=-1)
        self.votingClassifier.fit(self.X_train_features, self.y_train)
        models_info['voting'] = {
            'name': 'Voting',
            'model': self.votingClassifier,
            'param_grid': None,
            'best_param': None,
            'grid_score': None,
            'cross_val_score': None,
            'weight': None,
            'evaluation': None
        }
        return self.votingClassifier

    def split_predict(self): #not working yet, num. of feature don't match
        print(self.text_features.shape)
        print(self.X_test_features.shape)
        print(self.url_X_test_features.shape)
        self.input_url = ' '.join(re.findall(r'(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)', self.input_message))
        self.url_features = self.tfidf_vectorizer.transform([self.input_url])
        # If the URL is missing, create a placeholder
        print(self.url_features.shape)
        print(self.url_X_train_features.shape)
        print(self.X_train_features.shape)
        if self.url_features.shape[0] == 0:
            placeholder = csr_matrix((1, self.url_X_train_features.shape[1]))
            self.url_features = placeholder
        # Merge the text and URL features
        self.input_message_features = hstack([self.text_features, self.url_features])
        print(self.url_features.shape)
        print(self.input_message_features.shape)

        # PCA optional
        #self.input_message_features = self.pca_reduce(self.input_message_features)

    # Make prediction
    def make_predict(self, model):
        self.text_features = self.tfidf_vectorizer.transform([self.input_message])
        #if reduce:
            #self.predict_dim_reduce(self.text_features)
        #self.split_predict()
        #if self.input_message_features != None:
            #features = self.input_message_features
        #else:
            #features = self.text_features
        self.prediction = model.predict(self.text_features)
        #self.confidence = model.predict_proba(self.input_message_features)[:, 1]
        return self.prediction
    def print_predict(self):
        print(self.prediction)
    
    
    # Format result    
    def get_result(self):
        if self.prediction[0] == 0:
            self.result = 'Ham mail'
        elif self.prediction[0] == 1:
            self.result = 'Smishing Mail'
        else:
            self.result = 'Spam Mail'
    def print_result(self):
        self.get_result()
        print(self.result)

    def keep_record(self, name, run_time, comments=None):
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
        record = [run_time, name, self.train_accuracy, self.test_accuracy, self.precision, self.recall, self.f1, cross_val_score, best_param, comments] 
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




'''
# Create new reloadable trained models (NOT WORKING??)
    def new_models(self):
        trained_models = []
        for name, model in models_list:
            self.model = model
            self.train_evaluate()
            trained_models.append((name, self))
            print(f"{name} ready!")

        # Save trained models
        for name, self in self.trained_models:
            joblib.dump(self, f"{name}_model.pkl")
            print(f"{name} model saved!")

    # Load saved models
    def load_models(self):
        trained_models = []
        for name, _ in models_list:
            trained_model = joblib.load(f"{name}_model.pkl")
            trained_models.append((name, trained_model))
            print(f"{name} model loaded!")

#pipeline.save_trained()

#trying to save and load model in utils not working yet
# Train the models
pipeline.trained_models = []
for name, model in models_list:
    pipeline.model = model
    pipeline.train_evaluate()
    pipeline.trained_models.append((name, pipeline))
    print(f"{name} ready!")

            
     # Train model
    def train_model(self):
        self.model.fit(self.X_train_features, self.y_train)

        # Define models (testing in progress)
        self.MA = MultinomialNB(alpha=1.0, fit_prior=False)
        self.MB = AdaBoostClassifier(n_estimators=100)
        self.MC = RandomForestClassifier(n_estimators=100)
        self.MD = MLPClassifier(early_stopping=True, batch_size=128, verbose=False)
        self.ME = BernoulliNB(alpha=1.0, fit_prior=False)
        self.MF = DecisionTreeClassifier(max_depth=None)
        self.MG = KNeighborsClassifier(n_neighbors=3, weights='uniform')
        self.MH = LogisticRegression(max_iter=1000, C=10.0, penalty='l2') # penalty:['l1', 'l2', 'none']
        self.MI = SVC(random_state = 42)
        self.models = [self.MA, self.MB, self.MC, self.MD, self.ME, self.MF, self.MG, self.MH, self.MI]
        self.objects = ('MultiNB', 'ADB', 'RF', 'MLP', 'BNB', 'DT')

    def visualise_data(self):
        # Visualize the dataset
        plt.figure(figsize=(12,14))
        sns.countplot(x = self.df['LABEL'])
        plt.xlabel('LABEL')
        plt.title('Number of ham, spam, and smishing messages')
        plt.show()

    def cross_validation(self):
        num_folds = 5
        best_scores = {}
        best_params = {}
        model = self.trained_model.model
        for name, (model, param_grid) in models_param_grid.items():
            grid_search = GridSearchCV(model, param_grid, cv=num_folds, return_train_score=True)
            grid_search.fit(self.X_train_features, self.y_train)

            # Store the best score and best parameters for each model
            best_scores[name] = grid_search.best_score_
            best_params[name] = grid_search.best_params_

            print(f"Best parameters for {name}: {best_params[name]}")
            print(f"Best cross-validation score for {name}: {best_scores[name]}")

        return best_scores, best_params

        # Save trained models
    def save_trained(self, model):
        self.trained_models = []
        for name, model in models:
            self.model = model
            self.train_evaluate()
            self.trained_models.append((name, self.model))
            print(f"{name} saved!")

            

 for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")

    for name, trained_model in pipeline.trained_models:
        pipeline.input_message = message
        pipeline.make_predict()
        pipeline.print_format_result()
        all_predictions.append((name, pipeline.prediction[0]))
    
    # Aggregate predictions 
    hard_voting(all_predictions)
'''   



