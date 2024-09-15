#Loading Data     |--------------------------------------------------------
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
#Allows us to represent text as numerical values
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
#Provides a performance analytics capabilities
from sklearn.metrics import accuracy_score

df = pd.read_csv('DatasetCombined.csv', encoding='ISO-8859-1')
#print(df)

data = df.where((pd.notnull(df)),'')
#print(data.head)
#print(data.info())
#print(data.shape)

data.loc[data['LABEL'] == 'spam', 'LABEL',] = 2
data.loc[data['LABEL'] == 'smishing', 'LABEL',] = 1
data.loc[data['LABEL'] == 'ham', 'LABEL',] = 0

x = data['TEXT']
y = data['LABEL']
#print(x)
#print(y)

#Working With Data |--------------------------------------------------------
#20% of data used for test
X_train, X_test, Y_train, Y_test = train_test_split(x,y, test_size=0.2, random_state=100)
#print(x.shape)
#print(X_train.shape)
#print(X_test.shape)
#print(y.shape)
#print(Y_train.shape)
#print(Y_test.shape)

#Transform TEXT column from string (unreadable data) to a vector which is readable
feature_extraction = TfidfVectorizer(min_df=1, stop_words = 'english', lowercase=True)

#removes words such as the and as which are not as useful to the training of the algorithm
X_train_features = feature_extraction.fit_transform(X_train)
X_test_features = feature_extraction.transform(X_test)

Y_train = Y_train.astype('int')
Y_test = Y_test.astype('int')

#print(X_train)
#print(X_test)

#print(Y_train)
#print(Y_test)

#print(X_train_features)

#Creating the Model |---------------------------------------------------------
model = LogisticRegression()
model.fit(X_train_features, Y_train)

prediction_on_training_data = model.predict(X_train_features)
accuracy_on_training_data = accuracy_score(Y_train, prediction_on_training_data)

print('Accuracy on training data: ',accuracy_on_training_data)

prediction_on_test_data = model.predict(X_test_features)
accuracy_on_test_data = accuracy_score(Y_test, prediction_on_test_data)

print('Accuracy on test data: ', accuracy_on_test_data)

#input_your_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']
input_your_message = [input("Enter message: ")]

input_data_features= feature_extraction.transform(input_your_message)

prediction = model.predict(input_data_features)

print(prediction)


if(prediction[0]==0):
    print('Ham mail')
elif(prediction[0]==1):
    print('Smishing Mail')
else:
    print('Spam Mail')
    




