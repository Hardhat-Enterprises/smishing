# import pickle
# import pandas as pd
# from sklearn.feature_extraction.text import TfidfVectorizer

# def predict_messages(input):
#     # Load the trained model
#     with open('model_pickle','rb') as f:
#         mp = pickle.load(f)
#
#     # Load the TF-IDF vectorizer used for training
#     with open('vectorizer_pickle', 'rb') as f:
#         vectorizer = pickle.load(f)
#
#     prediction_on_test_data = model.predict(X_test_features)
#     accuracy_on_test_data = accuracy_score(Y_test, prediction_on_test_data)
#
#     input_your_message = input
#
#     input_data_features= feature_extraction.transform(input_your_message)
#
#     prediction = model.predict(input_data_features)
#
#
#     if(prediction[0]==0):
#         pred_updated = 'Harmless mail'
#     elif(prediction[0]==1):
#         pred_updated = 'Smishing Mail'
#     else:
#         pred_updated = 'Spam Mail'
#
#     return pred_updated
def predict_messages(input):
    input = "aaaaaa"

    return input