package com.example.smishingdetectionapp.viewmodel

import androidx.lifecycle.*
import com.example.smishingdetectionapp.models.FaqItem
import com.example.smishingdetectionapp.repository.FaqRepository
import kotlinx.coroutines.launch

class FaqViewModel(private val repository: FaqRepository) : ViewModel() {

    private val _faqList = MutableLiveData<List<FaqItem>>()
    val faqList: LiveData<List<FaqItem>> = _faqList

    fun loadFaqs() {
        viewModelScope.launch {
            try {
                _faqList.value = repository.fetchFaqs()
            } catch (e: Exception) {
                // handle errors here
            }
        }
    }
}
