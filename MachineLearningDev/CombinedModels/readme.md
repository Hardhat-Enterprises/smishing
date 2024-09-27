**Update: T2 2024**

**Folder 'Combine_models+url+feature'**



---



## Combine model testing - Created T1 2024

### 3 versions:

1. utils.py + list_of_functions.py + **msg_predict_main.py**  
   using class, predict whole message  
   (The main algorithm right now, may still need fine-tuning and experimenting, accuracy rates around 95%, future plan is to pickle the voting model and put in the app, or combine with the url models)

2. utils.py + list_of_functions.py + **url_separated_dataset.py**  
   using class, training data is the separated urls, and predicted urls

3. utils2.py + list_of_functions2.py +**url_predict_main.py**  
   not using class and need to pass values around, use online dataset (in a different format and separated), predict urls

### User guide:

Run only **msg_predict_main.py** or **url_separated_dataset.py** or **url_predict_main.py** and they'll use the corresponding utils and list of functions  

**run_files.ipynb**: run msg_predict_main.py in google colab

Models list - choose what models to use, comment out in the list the ones not using  
Once run, they'll loop over all the models in the *models* list, get corresponding *param_grid* dictionary for parameter tuning, and save everything and related results in the *models_info* dictionary and can be retrieved to use in places  
After training and before testing sample messages, a graph will pop up showing the accuracy scores result of everything. **This graph must be closed for the rest of the code to run (in vscode)** (you can save the graph)  
The training accuracy scores and time etc., and predicting values (mainly about speed) are saved in the test_log.csv, it's mainly for self reviewing purposes. When calling *keep_record()* can put any comments there.

---

Some other files outside this folder are experimentations (T1 2024)
including learning models, feature engineering techniques, dimensionality reduction techniques and the serialized files created by pickle
