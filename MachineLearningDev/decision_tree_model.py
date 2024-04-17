#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Apr  9 10:22:14 2024

@author: gladizgregory2
"""
from utils import *
from sklearn.tree import DecisionTreeClassifier  # Importing Decision Tree Classifier

model = DecisionTreeClassifier()
pipeline = ModelPipeline(model)

pipeline.load_dataset()
pipeline.input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']

pipeline.run_model_pipeline()
print(pipeline.prediction)

pipeline.print_results()


# Run the main function
#if __name__ == "__main__":
#    main()