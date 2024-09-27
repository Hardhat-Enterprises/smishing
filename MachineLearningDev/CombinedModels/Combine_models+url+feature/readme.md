# Combine models + Feature engineering + URL model (T2 2024)

*Support Vector, voting classifier, url vectorizer and  url classifier pickles are too large to commit to GitHub

## Content:

 **Main file:**

    **feature_engineering_url_combined_models.ipynb**

- Notable features:
  
  - URL extraction
  
  - URL detected as feature
  
  - Text features
  
  - Outliers detection

**Files used in the main file:**

    URL vectorizer and classifier: placeholder from previous url votingClassier in T1 (can be updated)

**Pickle folder:**

    Save states of trained individual models, and trained votingClassifier (use recall of smishing as weight)

## Evaluation

![scores.png](I:\My%20Drive\Colab%20Notebooks\smishingAI\MachineLearningDev\CombinedModels\Combine_models+url+feature\scores.png)

## 

## Topology

![topo.png](I:\My%20Drive\Colab%20Notebooks\smishingAI\MachineLearningDev\CombinedModels\Combine_models+url+feature\topo.png)

*now only has one focus on recall*
