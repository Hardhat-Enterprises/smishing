package com.example.smishingdetectionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smishingdetectionapp.repository.FaqRepository

class FaqViewModelFactory(private val repo: FaqRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        FaqViewModel(repo) as T
}
