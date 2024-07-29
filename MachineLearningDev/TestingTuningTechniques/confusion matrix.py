import pandas as pd
import numpy as np

# Load the CSV file into a DataFrame
df = pd.read_csv('test_log.csv')

# Add a new column to the DataFrame
df['comments'] = np.nan

df.to_csv('test_log.csv', index=False)

# Write the DataFrame back to the CSV file

# Confusion matrix
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import confusion_matrix, accuracy_score

# Assuming y_true and y_pred are your true and predicted labels
y_true = [0, 1, 2, 0, 1, 2]  # Replace with your actual labels
y_pred = [0, 1, 2, 1, 0, 2]  # Replace with your predicted labels
y_true = df['label']  # Assuming 'label' is the column with actual labels
# Assuming 'model' is your trained model and 'X_test' is your test data
y_pred = model.predict(X_test)

# Calculate the confusion matrix
conf_matrix = confusion_matrix(y_true, y_pred)

# Calculate the accuracy score
acc_score = accuracy_score(y_true, y_pred)

# Create a heatmap for the confusion matrix
plt.figure(figsize=(8, 6))
sns.heatmap(conf_matrix, annot=True, fmt='d', cmap='Blues')

# Add labels and title
plt.ylabel('Actual Label')
plt.xlabel('Predicted Label')
plt.title(f'Confusion Matrix\nAccuracy Score: {acc_score:.5f}')

# Display the plot
plt.show()
