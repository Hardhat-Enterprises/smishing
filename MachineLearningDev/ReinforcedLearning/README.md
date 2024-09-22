**Smishing Detection using the tool of Q - Learning**

This project and concept implements a Q (learning) system that is used to detect SMS phisihing messages. The core focus of this model is to obtain user feedback, as this will help improve the accuracy over a period of time. The model highlights the message as either smishing or ham, whilst providing the user the opportunity to correct the AI.

**Features**
Q- learning algorithm to incorporate RL
Provides the user a chance to give feedbacm
Utilises TF-IDF to vectorize
Includes user created sample messages
Reward system

**Required Tools** (within python)
numpy
scikit-learn

**Example Output**
Message that has been received: You have won a free lottery! Click here to claim your prize.
This message has been specifically classified as: Smishing
Hi User! Was this correct? (yes/no): no
OH my bad! What is the correct classification (Remember 1 is for smishing, 0 is for ham): 1

