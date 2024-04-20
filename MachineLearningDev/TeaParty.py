from utils import *

pipeline = ModelPipeline()

pipeline.load_process_dataset()

#pipeline.save_trained()

'''
# Train the models
pipeline.trained_models = []
for name, model in models_list:
    pipeline.model = model
    pipeline.train_evaluate()
    pipeline.trained_models.append((name, pipeline))
    print(f"{name} ready!")
'''
pipeline.cross_validation()
pipeline.train_evaluate()
cross_val_scores = pipeline.evaluate_with_cross_validation()
print(cross_val_score)

for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")

    for name, trained_model in pipeline.trained_models:
        pipeline.input_message = message
        pipeline.test_predict()
        pipeline.print_format_result()
        all_predictions.append((name, pipeline.prediction[0]))
    
    # Aggregate predictions 
    voting(all_predictions)


