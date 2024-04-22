import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from imblearn.over_sampling import SMOTE
from imblearn.under_sampling import RandomUnderSampler
from imblearn.pipeline import Pipeline
from collections import Counter
from sklearn.model_selection import train_test_split, GridSearchCV, RandomizedSearchCV, cross_val_score
from collections import defaultdict
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import accuracy_score
from sklearn.datasets import load_digits
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import GridSearchCV
# Models
from sklearn.neighbors import KNeighborsClassifier 
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
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
]

models_list = [
    ("Naive Bayes multinomia", MultinomialNB(alpha=1.0,fit_prior=False)),
    ("AdaBoost", AdaBoostClassifier(n_estimators=100)),
    ("random forest", RandomForestClassifier(n_estimators=100)),
    ("Multi-layer Perceptron", MLPClassifier(early_stopping=True, batch_size=128, verbose=False)),
    ("naive bayes multivariate Bernoulli", BernoulliNB(alpha=1.0, fit_prior=False)),
    ("decision tree", DecisionTreeClassifier()),
    ("knn model", KNeighborsClassifier()), 
    ("logistic regression", LogisticRegression()),
    ("Support Vector", SVC())
]

models_param_grid = {
    "Naive Bayes multinomial": (MultinomialNB(), {'alpha': [0.1, 1.0, 10.0], 'fit_prior': [True, False]}),
    "AdaBoost": (AdaBoostClassifier(), {'n_estimators': [50, 100, 200]}),
    "Random Forest": (RandomForestClassifier(), {'n_estimators': [50, 100, 200]}),
    "Multi-layer Perceptron": (MLPClassifier(), {'hidden_layer_sizes': [(50,), (100,), (200,)], 'alpha': [0.0001, 0.001, 0.01],'max_iter': [500]}),
    "Naive Bayes multivariate Bernoulli": (BernoulliNB(), {'alpha': [0.1, 1.0, 10.0], 'fit_prior': [True, False]}),
    "Decision Tree": (DecisionTreeClassifier(), {'max_depth': [None, 10, 20, 50]}),
    "KNN": (KNeighborsClassifier(), {'n_neighbors': [3, 5, 10], 'weights': ['uniform', 'distance']}),
    "Logistic Regression": (LogisticRegression(), {'C': [0.1, 1.0, 10.0], 'penalty': ['l1', 'l2']}),
    "Support Vector": (SVC(), {'C': [0.1, 1.0, 10.0], 'gamma': [0.1, 1.0, 10.0]})
}

class ModelPipeline:
    def __init__(self):
        self.tfidf_vectorizer = None
        self.X_train = None
        self.X_test = None
        self.y_train = None
        self.y_test = None
        self.input_message = None
        self.input_message_features = None
        self.prediction = None
        self.model = None
        # Define models (testing in progress)
        self.MA = MultinomialNB(alpha=1.0,fit_prior=False)
        self.MB = AdaBoostClassifier(n_estimators=100)
        self.MC = RandomForestClassifier(n_estimators=100)
        self.MD = MLPClassifier(early_stopping=True, batch_size=128, verbose=False)
        self.ME = BernoulliNB(alpha=1.0, fit_prior=False)
        self.MF = DecisionTreeClassifier()
        self.MG = KNeighborsClassifier()
        self.MH = LogisticRegression()
        self.MI = SVC()
        self.models = [self.MA, self.MB, self.MC, self.MD, self.ME, self.MF, self.MG, self.MH, self.MI]
        self.objects = ('MultiNB', 'ADB', 'RF', 'MLP', 'BNB', 'DT')


    def load_dataset(self):
        dataset_path = 'DatasetCombined.csv'
        self.df = pd.read_csv(dataset_path, encoding='ISO-8859-1')
        # Create Dictionary
        map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
        self.df['LABEL'] = self.df['LABEL'].map(map_label)
        return self.df
    
    def see_dataset(self):
        sns.countplot(self.df['LABEL'])
        plt.xlabel('LABEL')
        plt.title('Number of ham, spam, and smishing messages')


    def split_dataset(self):
        # Split data into features (X) and labels (y)
        X = self.df['TEXT']
        y = self.df['LABEL']
        # Split data into training and testing sets
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(X, y, test_size=0.2, random_state=100)
        return self.X_train, self.X_test, self.y_train, self.y_test

    def feature_extraction(self):
        # Feature extraction using TF-IDF
        self.tfidf_vectorizer = TfidfVectorizer(min_df=1, stop_words='english', lowercase=True)
        self.X_train_features = self.tfidf_vectorizer.fit_transform(self.X_train)
        self.X_test_features = self.tfidf_vectorizer.transform(self.X_test)
        return self.X_train_features, self.X_test_features
    
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


    # Load and process the dataset
    def load_process_dataset(self):
        self.load_dataset()
        self.see_dataset()
        self.split_dataset()
        self.feature_extraction()
        self.balance_data()
        return self.X_train_features, self.y_train
        

    # Find best params
    def cross_validation(self):
        num_folds = 5
        best_scores = defaultdict(float)
        best_params = {}
        for name, (model, param_grid) in models_param_grid.items():
            if name == "Multi-layer Perceptron":
                # Use RandomizedSearchCV for Multi-layer Perceptron since it's way too slow
                grid_search = RandomizedSearchCV(model, param_distributions=param_grid, n_iter=100, cv=num_folds, return_train_score=True)
            else:
                # Use GridSearchCV for other models
                grid_search = GridSearchCV(model, param_grid, cv=num_folds, return_train_score=True)
            
            grid_search.fit(self.X_train_features, self.y_train)
            # Store the best score and best parameters for each model
            best_scores[name] = grid_search.best_score_
            best_params[name] = grid_search.best_params_

            print(f"Best parameters for {name}: {best_params[name]}")
            print(f"Best cross-validation score for {name}: {best_scores[name]}")
            print(f"{name} ready!")
        return best_scores, best_params

    def train_evaluate(self):
        for name, (model, _) in models_param_grid.items():
            model.fit(self.X_train_features, self.y_train)
            self.trained_models[name] = model
            return self.trained_model

    def evaluate_with_cross_validation(self):
        cross_val_scores = {}
        for name, model in self.trained_models.items():
            scores = cross_val_score(model, self.X_train_features, self.y_train, cv=5)
            cross_val_scores[name] = scores.mean()
            print(f"Cross-validation scores for {name}: {scores}")
        return cross_val_scores


    # Train model
    def train_model(self):
        self.model.fit(self.X_train_features, self.y_train)
    
    # Evaluate the model
    def evaluate_model(self):
        self.train_accuracy = accuracy_score(self.y_train, self.model.predict(self.X_train_features))
        self.test_accuracy = accuracy_score(self.y_test, self.model.predict(self.X_test_features))
        return self.train_accuracy, self.test_accuracy
    def print_accuracy(self):
        print('Accuracy on training data: ', self.train_accuracy)
        print('Accuracy on test data: ', self.test_accuracy)


    # Run train model pipeline
    def basic_train_evaluate(self):
        self.train_model()
        self.evaluate_model()
        self.print_accuracy()

    # Save trained models
    def save_trained(self):
        self.trained_models = []
        for name, model in models_list:
            self.model = model
            self.train_evaluate()
            self.trained_models.append((name, self.model))
            print(f"{name} ready!")

    # Make prediction
    def test_predict(self):
        self.input_message_features = self.tfidf_vectorizer.transform([self.input_message])
        self.prediction = self.model.predict(self.input_message_features)
        return self.prediction
    def print_predict(self):
        print(self.prediction)
    
    # Dataset and model
    def dataset_model(self):
        self.load_process_dataset()
        self.train_evaluate()
        return self.prediction
    

    # Format result    
    def print_results(self):
        if self.prediction[0] == 0:
            print('Ham mail')
        elif self.prediction[0] == 1:
            print('Smishing Mail')
        else:
            print('Spam Mail')

    def print_format_result(self):
        self.print_accuracy()
        self.print_predict()
        self.print_results()
        





'''
# Create new reloadable trained models (NOT WORKING)
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

'''   

# Print result
def print_results(prediction):
    if prediction[0] == 0:
        print('Ham mail')
    elif prediction[0] == 1:
        print('Smishing Mail')
    else:
        print('Spam Mail')

# Aggregate predictions
def voting(all_predictions):
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


