# We start with the imports
import numpy as np # for numerical values
import random # Too generate the random examples needed to train
from sklearn.model_selection import train_test_split #Splits the dataset
from sklearn.feature_extraction.text import TfidfVectorizer


#Creating the class, for the detection
class smishingenvdetect: # This will set up how the class is constructed
    def __init__(self, message, labeled):
        self.vectorizer = TfidfVectorizer() # Fix: instantiate the vectorizer
        self.message = message
        self.labeled = labeled
        self.X = self.vectorizer.fit_transform(self.message).toarray() # Fix: apply fit_transform on self.message
        self.y = np.array(labeled)
        self.not_actions = 2 # The two is because the options can either be smishing and spam
        self.q_learning_table = np.zeros((self.X.shape[0], self.not_actions))
        self.stating_index = None # Keeps track of the state

    def reset(self): # This is the step in which a message is randomly picked (in sure the state) to classify
        self.stating_index = random.randint(0, len(self.message)-1)
        return self.stating_index

    def step(self, action): # This gets the label for the current state
        correct_label = self.y[self.stating_index]
        reward = 0

        # This is where the reward is defined, either correct or incorrect classification
        if action == correct_label:
            reward = 1 # For correct !
        else:
            reward = -1 # For incorrect !

        done = True # one message per classification

        return self.stating_index, reward, done

    def chooseing_action(self, epsilon): # Fix: 'chooseing' can remain for your style
        if random.uniform(0, 1) < epsilon:
            action = random.choice([0, 1]) # this will explore the random classification
        else:
            action = np.argmax(self.q_learning_table[self.stating_index]) # exploiting
        return action

    def learning(self, stating_index, action, reward, alpha, gamma): # This incorporates Q learning
        next_state_index = random.randint(0, len(self.message) - 1) # Random next message
        best_conutined_action = np.max(self.q_learning_table[next_state_index])
        q_current = self.q_learning_table[stating_index, action]
        self.q_learning_table[stating_index, action] = q_current + alpha * (reward + gamma * best_conutined_action - q_current)

    def update_user_feedback(self, state_index, correct_action, alpha=0.1):
        """ # This is where the controller should place the feedback aka the correct classification and the learning rate
       # Update the Q-table based on user feedback.
       # :param state_index: The current state index.
       # :param correct_action: The correct classification provided by the user.
       # :param alpha: Learning rate for feedback update.
        """
        # This is where I have added the maximum reward, aka in this case 10, for correct action based on the feedback via user
        reward = 15 # High learning for learning from feedback
        q_current = self.q_learning_table[state_index, correct_action]
        self.q_learning_table[state_index, correct_action] = q_current + alpha * (reward - q_current)

    def training_AI_feedback(self, episodes=1000, alpha=0.1, gamma=0.9, epsilon=0.1): # Fix: self is needed to access class methods
        for episode in range(episodes):
            state_index = self.reset()
            done = False

            while not done:
                action = self.chooseing_action(epsilon)
                next_state_index, reward, done = self.step(action)

                # This is what the user is shown
                # Shows the classification
                print(f"Message that has been received: {self.message[state_index]}")
                if action == 1:
                    print(" This message has been specifically classified as: Smishing")
                else:
                    print(" This message has been specifically classified as: HAM")

                # Creating the feedback opportunity
                # Will ask the user
                Reaction = input("Hi User! Was this correct? (yes/no): ").strip().lower()

                # Below will take the user reaction
                if Reaction == 'no' or Reaction == 'NO': # The user now has to provide the correct classification (1 for smishing, 0 for ham)
                    corrected_action = int(input("OH my bad! What is the correct classification (Remember 1 is for smishing, 0 is for ham): "))
                    # provide this feedback to the Q table
                    self.update_user_feedback(state_index, corrected_action)
                else: # Learning update
                    self.learning(state_index, action, reward, alpha, gamma)

                state_index = self.reset() # Ensure the model moves on to the next message

            if (episode + 1) % 100 == 0:
                print(f"Episode {episode + 1}/{episodes} has been completed")

if __name__ == '__main__': # Main
    # This is an example dataset: SMS messages and will provide the labels
    messages = [
        "You have won a free lottery! Click here to claim your prize.",
        "Don't forget about the meeting tomorrow.",
        "Your account has been compromised. Change your password immediately.",
        "Hi, just wanted to check in and say hello.",
        "Get a free iPhone now by entering this contest!",
    ]
    labels = [1, 0, 1, 0, 1] # Again 1 for Smishing, 0 is for ham

    # Splitting the data into tests and for testing sets
    X_train, X_test, y_train, y_test = train_test_split(messages, labels, test_size=0.2, random_state=42)

    # we can now initialize the environment
    ENV = smishingenvdetect(X_train, y_train)

    # Now train the feedback
    ENV.training_AI_feedback(episodes=500)

    print("The Q-table has been successfully trained:")
    print(ENV.q_learning_table)
