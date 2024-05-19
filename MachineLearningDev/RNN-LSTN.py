# Tutorial: https://www.kaggle.com/code/kredy10/simple-lstm-for-text-classification/notebook
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from keras.models import Model
from keras.layers import LSTM, Activation, Dense, Dropout, Input, Embedding
from keras.optimizers import RMSprop
from keras.preprocessing import text
from keras.preprocessing import sequence
from keras.utils import to_categorical
from keras.callbacks import EarlyStopping
from utils import *
from keras.preprocessing.text import Tokenizer
from keras.preprocessing import sequence
from keras.models import Sequential

class LSTMClassifier:
    def __init__(self):
        self.max_words = 2000
        self.max_len = 300
        self.tokeniser = Tokenizer(num_words=self.max_words)

        self.model = Sequential([Input(name='inputs',shape=[self.max_len]),
        Embedding(self.max_words,50,input_length=self.max_len),
        LSTM(64),
        Dense(256,name='FC1'),
        Activation('relu'),
        Dropout(0.5),
        Dense(1,name='out_layer'),
        Activation('sigmoid')]);

###### N O T   W O R K I N G   Y E T ##########
'''
# Load data
df = pd.read_csv('DatasetCombined.csv',delimiter=',',encoding='latin-1')
df.head()

# Drop unneeded coloum
df.drop(['Unnamed: 2', 'Unnamed: 3', 'Unnamed: 4'],axis=1,inplace=True)
df.info()
'''

# load_dataset
dataset_path = 'DatasetCombined.csv'
df = pd.read_csv(dataset_path, encoding='ISO-8859-1')
# Create Dictionary
map_label = {'spam': 2, 'smishing': 1, 'ham': 0}
df['LABEL'] = df['LABEL'].map(map_label)



# Show distribution
sns.countplot(df.v1)
plt.xlabel('Label')
plt.title('Number of ham and spam messages')


# Split data into features (X) and labels (y)
X = df['TEXT']
y = df['LABEL']
# Split data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=100)


# Feature extraction using TF-IDF
tfidf_vectorizer = TfidfVectorizer(min_df=1, stop_words='english', lowercase=True)
X_train_features = tfidf_vectorizer.fit_transform(X_train)
X_test_features = tfidf_vectorizer.transform(X_test)

    
# Balance data
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


# Create input output
# Process label
X = df.v2
Y = df.v1
le = LabelEncoder()
Y = le.fit_transform(Y)
Y = Y.reshape(-1,1)

# Split data
X_train,X_test,Y_train,Y_test = train_test_split(X,Y,test_size=0.15)


# Process data
max_words = 1000
max_len = 150
tok = Tokenizer(num_words=max_words)
tok.fit_on_texts(X_train)
sequences = tok.texts_to_sequences(X_train)
sequences_matrix = sequence.pad_sequences(sequences,maxlen=max_len)


# RNN
def RNN():
    inputs = Input(name='inputs',shape=[max_len])
    layer = Embedding(max_words,50,input_length=max_len)(inputs)
    layer = LSTM(64)(layer)
    layer = Dense(256,name='FC1')(layer)
    layer = Activation('relu')(layer)
    layer = Dropout(0.5)(layer)
    layer = Dense(1,name='out_layer')(layer)
    layer = Activation('sigmoid')(layer)
    model = Model(inputs=inputs,outputs=layer)
    return model

# Compile model
model = RNN()
model.summary()
model.compile(loss='binary_crossentropy',optimizer=RMSprop(),metrics=['accuracy'])

# Fit on training data
model.fit(sequences_matrix,Y_train,batch_size=128,epochs=10,
          validation_split=0.2,callbacks=[EarlyStopping(monitor='val_loss',min_delta=0.0001)])

# Process test data
test_sequences = tok.texts_to_sequences(X_test)
test_sequences_matrix = sequence.pad_sequences(test_sequences,maxlen=max_len)

# Evaluate data on test set
accr = model.evaluate(test_sequences_matrix,Y_test)

print('Test set\n  Loss: {:0.3f}\n  Accuracy: {:0.3f}'.format(accr[0],accr[1]))