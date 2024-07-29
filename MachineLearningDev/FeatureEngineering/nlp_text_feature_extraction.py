from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from gensim.models import Word2Vec, KeyedVectors

# Sample documents
documents = ["This is the first document.",
             "This document is the second document.",
             "And this is the third one.",
             "Is this the first document?"]

# Bag-of-Words representation
count_vectorizer = CountVectorizer()
bow_matrix = count_vectorizer.fit_transform(documents)
print("Bag-of-Words Matrix:")
print(bow_matrix.toarray())

# TF-IDF representation
tfidf_vectorizer = TfidfVectorizer()
tfidf_matrix = tfidf_vectorizer.fit_transform(documents)
print("\nTF-IDF Matrix:")
print(tfidf_matrix.toarray())

# Word2Vec
word2vec_model = Word2Vec([doc.split() for doc in documents], vector_size=100, window=5, min_count=1, workers=4)
print("\nWord Embedding for 'document':")
print(word2vec_model.wv['document'])

# Load pre-trained Word2Vec embeddings
# Load pre-trained Word2Vec embeddings
word2vec_model = KeyedVectors.load_word2vec_format('path_to_word2vec.bin', binary=True)

# Example sentence
sentence = "example sentence to get word embeddings"

# Get word embeddings for the sentence
sentence_embeddings = []
for word in sentence.split():
    if word in word2vec_model.vocab:
        # If the word exists in the vocabulary, get its embedding
        word_embedding = word2vec_model[word]
        sentence_embeddings.append(word_embedding)

# Now sentence_embeddings contains embeddings for each word in the sentence
# You can use these embeddings as features for your text data


# Load pre-trained Word2Vec embeddings
word2vec_model = KeyedVectors.load_word2vec_format('path_to_word2vec.bin', binary=True)

# Example pre-processed input text
input_text = "example preprocessed input text"

# Aggregate word embeddings for the input text (e.g., by averaging)
input_embeddings = []
for word in input_text.split():
    if word in word2vec_model.vocab:
        input_embeddings.append(word2vec_model[word])
# If there are no word embeddings found for the input text, handle it appropriately
if not input_embeddings:
    # Handle case where input text does not contain any words in the vocabulary
    pass
else:
    # Aggregate word embeddings (e.g., by averaging)
    aggregated_embeddings = sum(input_embeddings) / len(input_embeddings)

    # Use aggregated embeddings as features for prediction
    X_test = [aggregated_embeddings]

    # Load trained logistic regression model
    # model = load_model('trained_model.pkl')

    # Perform prediction using the model
    # y_pred = model.predict(X_test)
