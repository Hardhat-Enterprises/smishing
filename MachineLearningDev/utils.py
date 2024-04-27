import joblib
import pandas as pd
import numpy as np	
import re
import matplotlib.pyplot as plt
import seaborn as sns
from imblearn.over_sampling import SMOTE
from imblearn.under_sampling import RandomUnderSampler
from imblearn.pipeline import Pipeline
from collections import Counter
from sklearn.model_selection import train_test_split, GridSearchCV, RandomizedSearchCV, cross_val_score
from sklearn.ensemble import VotingClassifier
from collections import defaultdict
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import accuracy_score
from sklearn.datasets import load_digits
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import GridSearchCV
from joblib import parallel_backend
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.decomposition import PCA
from sklearn.decomposition import TruncatedSVD
from sklearn.decomposition import NMF
from scipy.sparse import csr_matrix, hstack, vstack
# Models
from sklearn.neighbors import KNeighborsClassifier 
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
#for gpu acceleration
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
    "Your parcel cannot be delivered due to an incorrect address. Please update your details https://postmab.life/au",
    "Your $5.83 road toll bill is overdue, please process it online as soon as possible to avoid facing hefty fines https://linxaui.life/au",
    "Your parcel cannot be delivered due to an incorrect address. Please update your details https://postmab.life/au",
    "AusPost:Your shipping address is invalid, Post Shop delivery has be deliveries have been stopped, more details:https://aupost.mypoca.services",
    "[DiDi]$15 off next 2 rides!* Savings automatically applied on your next ride request. Until Sunday. https://dd.me/k3M7s23 Opt out: https://dd.me/b6kHCl5",
    "JUST ANNOUNCED Big Apple $750 Scholarship Study in New York State (USA) this July. 4-week program designed for Australian Uni students! www.cisaustalia.com.au"


]

models = [
    ("Naive Bayes multinomial", MultinomialNB()),
    ("AdaBoost", AdaBoostClassifier()),
    ("Random Forest", RandomForestClassifier()),
    #("Multi-layer Perceptron", MLPClassifier()),
    ("Naive Bayes multivariate Bernoulli", BernoulliNB()),
    ("Decision Tree", DecisionTreeClassifier()),
    ("KNN", KNeighborsClassifier()), 
    ("Logistic Regression", LogisticRegression()),
    ("Support Vector", SVC())
]

param_grid = {
    "Naive Bayes multinomial": {'alpha': [0.1, 1.0, 10.0], 'fit_prior': [True, False]},
    "AdaBoost": {'n_estimators': [50, 100, 200]},
    "Random Forest": {'n_estimators': [50, 100], 'max_depth': [5, 10], 'min_samples_split': [2, 5, 10], 'min_samples_leaf': [1, 2, 4], 'max_features': ['sqrt', 'log2'], 'n_jobs': [-1]},
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


    def load_dataset(self):
        dataset_path = 'DatasetCombined.csv'
        self.df = pd.read_csv(dataset_path, encoding='ISO-8859-1')
        # Create Dictionary
        map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
        self.df['LABEL'] = self.df['LABEL'].map(map_label)
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
        # Split data into training and testing sets
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(X, y, test_size=0.2, random_state=100)
        return self.X_train, self.X_test, self.y_train, self.y_test
    
    def feature_extraction(self):
        # Feature extraction using TF-IDF
        self.tfidf_vectorizer = None
        self.tfidf_vectorizer = TfidfVectorizer(min_df=1, stop_words='english', lowercase=True)
        self.X_train_features = self.tfidf_vectorizer.fit_transform(self.X_train)
        self.X_test_features = self.tfidf_vectorizer.transform(self.X_test)
        return self.X_train_features, self.X_test_features
        
    def split_text_and_link(self):
        self.split_dataset("TEXT")
        self.text_X_train_features, self.text_X_test_features = self.feature_extraction()
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
    def dimensionality_reduction(self):
        # Apply NMF
        self.nmf = NMF(n_components=100)
        self.X_train_features = self.nmf.fit_transform(self.X_train_features)
        self.X_test_features = self.nmf.transform(self.X_test_features)
        
    def pca_reduce(self, feature_input):
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
        grid_search = GridSearchCV(model, param_grid, cv=num_folds, return_train_score=True)
        with parallel_backend('threading'):
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

    
    # Evaluate the model
    def evaluate_model(self, name, model):
        self.train_accuracy = accuracy_score(self.y_train, model.predict(self.X_train_features))
        self.test_accuracy = accuracy_score(self.y_test, model.predict(self.X_test_features))
        self.precision = precision_score(self.y_test, model.predict(self.X_test_features), average='weighted')
        self.recall = recall_score(self.y_test, model.predict(self.X_test_features), average='weighted')
        self.f1 = f1_score(self.y_test, model.predict(self.X_test_features), average='weighted')
        models_info[name]['evaluation'] = [self.train_accuracy, self.test_accuracy, self.precision, self.recall, self.f1]
        print('Accuracy on training data: '.ljust(30), self.train_accuracy)
        print('Accuracy on test data: '.ljust(30), self.test_accuracy)
        print('Precision: '.ljust(30), self.precision)
        print('Recall: '.ljust(30), self.recall)
        print('F1: '.ljust(30), self.f1)

    def visualise_data(self):
        metrics = list(next(iter(models_info.values()))['evaluation'].keys())
        # Create a bar plot for each metric
        for metric in metrics:
            # Get the values for this metric for each model
            values = [info['evaluation'][metric] for info in models_info.values()]
            plt.figure(figsize=(10, 5))
            # Create an array with the positions of each bar on the x-axis
            x_pos = np.arange(len(models_info.keys()))
            # Create a bar plot
        plt.bar(x_pos, values, align='center')
        # Replace the x-ticks with the model names
        plt.xticks(x_pos, models_info.keys())
        # Add labels and title
        plt.xlabel('Models')
        plt.ylabel(metric)
        plt.title(f'{metric} comparison')
        # Show the plot
        plt.show()

# For model list
    def cross_validation(self):
        for name, model in models:
            scores = cross_val_score(model, self.X_train_features, self.y_train, cv=5, scoring='accuracy')
            models_info[name]['cross_val_score'] = scores.mean()
            print(f"Cross-validation scores for {name}: ".ljust(65), models_info[name]['cross_val_score'])
        total_score = sum(model['cross_val_score'] for model in models_info.values())
        for name, model in models_info.items():
            model['weight'] = model['cross_val_score'] / total_score
            print(f"Weight for {name}:".ljust(50), model['weight'])
# For model list
    def train_voting_model(self):
        weights = [models_info[name]['weight'] for name, _ in models]
        self.votingClassifier = VotingClassifier(estimators=models, voting='soft', weights=weights, verbose=True)
        self.votingClassifier.fit(self.X_train_features, self.y_train)


    def split_predict(self):
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
        #self.split_predict()
        if self.input_message_features != None:
            features = self.input_message_features
        else:
            features = self.text_features
        self.prediction = model.predict(features)
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



