package com.example.smishingdetectionapp.data

object FeedbackMemoryStore {

    private val feedbackList = mutableListOf<String>()

    fun addFeedback(feedback: String) {
        feedbackList.add(feedback)
    }

    fun getFeedbackHistory(): List<String> {
        return feedbackList.toList()
    }

    fun updateFeedback(index: Int, newEntry: String) {
        if (index >= 0 && index < feedbackList.size) {
            feedbackList[index] = newEntry
        }
    }

    fun removeFeedback(feedback: String) {
        feedbackList.remove(feedback)
    }

    fun updateFeedback(oldEntry: String, newEntry: String) {
        val index = feedbackList.indexOf(oldEntry)
        if (index != -1) {
            feedbackList[index] = newEntry
        }
    }

    fun clearAllFeedback() {
        feedbackList.clear()
    }
}
