#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Apr  9 10:20:37 2024

@author: gladizgregory2
"""

from utils import *
from sklearn.neighbors import KNeighborsClassifier  


pipeline = ModelPipeline(KNeighborsClassifier())

pipeline.input_message = ['Please Stay At Home. To encourage the notion of staying at home. All tax-paying citizens are entitled to ï¿½305.96 or more emergency refund. smsg.io/fCVbD']

pipeline.run_model_pipeline(0.2, 100, 1)
print(pipeline.prediction)

pipeline.print_results()


