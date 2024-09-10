import streamlit as st 
import pandas as pd 
import numpy as np 
import joblib 
import nltk 

from nltk.corpus import stopwords 
from nltk.tokenize import word_tokenize 
from nltk.stem import WordNetLemmatizer 

import re 
import ast 
import string 
from tensorflow.keras.models import load_model 

#original app 1.py from Teams

nltk.download('wordnet')
nltk.download('stopwords')
nltk.download('omw-1.4')
nltk.download('punkt')

# open chatwords.txt
with open('chatwords.txt') as f:
    data = f.read()
chatwords = ast.literal_eval(data)

# open abbreviation.txt
with open('abbreviation.txt') as abb:
    ab2 = abb.read()
abbreviation = ast.literal_eval(ab2)

# define stopwords
stop_words = stopwords.words('english')

# define lemmatizer
lem = WordNetLemmatizer()

# load model
final_gru = tf.keras.models.load_model('model_gru')

# import functions
def check_chatwords(text):
    temp=[]
    for chat in text.split():
        if chat.upper() in chatwords:
            temp.append(chatwords[chat.upper()])
        else:
            temp.append(chat)
    return " ".join(temp)

def lower(text):
    data = text.lower()
    return data 

def check_abbr(text):
    temp2=[]
    for abbr in text.split():
        if abbr in abbreviation:
            temp2.append(abbreviation[abbr])
        else:
            temp2.append(abbr)

    return " ".join(temp2)

def check_punctuation(text):
    data = re.sub("[^a-zA-Z]",' ', text)
    data = re.sub("[[^]]*]", ' ', data)
    data = re.sub(r"\n", " ", data)
    data = data.strip()
    data = ' '.join(data.split())
    return data

def token_stopwords_lemma(text):
    tokens = word_tokenize(text)
    stop_words2 = ' '.join([word for word in tokens if word not in stop_words])
    data = [lem.lemmatize(word) for word in stop_words2.split()]
    data = ' '.join(data)
    return data 

st.title("SPAM Message Detection")

message = st.text_input('Please input your message here (in English):')
st.write('Message:', message)

df_inf = [message]
df_inf1 = pd.DataFrame()
df_inf1['message'] = df_inf

df_inf1['message'] = df_inf1['message'].apply(lambda j: check_chatwords(j))
df_inf1['message'] = df_inf1['message'].apply(lambda k: lower(k))
df_inf1['message'] = df_inf1['message'].apply(lambda v: check_abbr(v))
df_inf1['message'] = df_inf1['message'].apply(lambda r: check_punctuation(r))
df_inf1['message'] = df_inf1['message'].apply(lambda m: token_stopwords_lemma(m))

y_pred_inf = final_gru.predict(df_inf1['message'])
y_pred_inf = np.where(y_pred_inf >= 0.5, 1, 0)

# Membuat dataframe dari array 
pred_df = pd.DataFrame(y_pred_inf, columns=['label'])

# Melakukan prediksi pada new dataframe
df_inf2 = pd.DataFrame(df_inf, columns=['message'])
df_combined = pd.concat([df_inf2, pred_df], axis=1)

# Predict
if st.button('Predict'):
    y_pred_inf = final_gru.predict(df_inf1['message'])
    y_pred_inf = np.where(y_pred_inf >= 0.5, 1, 0)
    spam_status = str(y_pred_inf[0][0])

    if spam_status == "0":
        st.success("Your message is not spam.")
    else:
        st.error("Your message is spam")