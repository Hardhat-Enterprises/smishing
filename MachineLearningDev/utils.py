import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import accuracy_score
#from sklearn.tree import DecisionTreeClassifier  # Importing Decision Tree Classifier
#from sklearn.neighbors import KNeighborsClassifier  

class ModelPipeline:
    def __init__(self, model):
        self.model = model
        self.dataset_path = None
        self.tfidf_vectorizer = None
        self.X_train = None
        self.X_test = None
        self.y_train = None
        self.y_test = None
        self.input_message = None
        self.input_message_features = None
        self.prediction = None
        
    
    # Load the dataset
    def load_dataset(self, dataset_path='DatasetCombined.csv'):
        self.df = pd.read_csv(dataset_path, encoding='ISO-8859-1')

    def process_data (self, test_size=0.2, random_state=100, min_df=1):
        # updated for testing parameters, prefer anything else?
        # Create Dictionary
        map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
        self.df['LABEL'] = self.df['LABEL'].map(map_label)

        # Split data into features (X) and labels (y)
        X = self.df['TEXT']
        y = self.df['LABEL']

        # Split data into training and testing sets
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(X, y, test_size=test_size, random_state=random_state)

        # Feature extraction using TF-IDF
        self.tfidf_vectorizer = TfidfVectorizer(min_df=min_df, stop_words='english', lowercase=True)
        self.X_train_features = self.tfidf_vectorizer.fit_transform(self.X_train)
        self.X_test_features = self.tfidf_vectorizer.transform(self.X_test)
    
    # Train model
    def train_model(self):
        self.model.fit(self.X_train_features, self.y_train)
    
    # Evaluate the model
    def evaluate_model(self):
        self.train_accuracy = accuracy_score(self.y_train, self.model.predict(self.X_train_features))
        self.test_accuracy = accuracy_score(self.y_test, self.model.predict(self.X_test_features))

    # Make prediction
    def test_predict(self):
        self.input_message_features = self.tfidf_vectorizer.transform(self.input_message)
        self.prediction = self.model.predict(self.input_message_features)

    # Format result
    def print_accuracy(self):
        print('Accuracy on training data: ', self.train_accuracy)
        print('Accuracy on test data: ', self.test_accuracy)
        print(self.prediction)
    
    def print_results(self):
        if self.prediction[0] == 0:
            print('Ham mail')
        elif self.prediction[0] == 1:
            print('Smishing Mail')
        else:
            print('Spam Mail')

    def print_format_result(self):
        self.print_accuracy()
        self.print_results()
        
            
    # Whole process
    def run_model_pipeline(self, test_size=0.2, random_state=100, min_df=1):
        self.load_dataset()
        self.process_data(test_size, random_state, min_df)
        self.train_model()
        self.evaluate_model()
        result = self.test_predict()
        return result




