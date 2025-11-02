package com.example.smishingdetectionapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smishingdetectionapp.R
import com.example.smishingdetectionapp.network.RetrofitInstance
import com.example.smishingdetectionapp.repository.FaqRepository
import com.example.smishingdetectionapp.ui.faq.FaqAdapter
import com.example.smishingdetectionapp.viewmodel.FaqViewModel
import com.example.smishingdetectionapp.viewmodel.FaqViewModelFactory
import androidx.recyclerview.widget.RecyclerView

class FaqActivity : ComponentActivity() {
    private lateinit var viewModel: FaqViewModel
    private val adapter = FaqAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        val rv = findViewById<RecyclerView>(R.id.faqRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        val repo = FaqRepository(RetrofitInstance.api)
        val factory = FaqViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[FaqViewModel::class.java]

        viewModel.faqList.observe(this) { list -> adapter.submit(list) }
        viewModel.loadFaqs()
    }
}
