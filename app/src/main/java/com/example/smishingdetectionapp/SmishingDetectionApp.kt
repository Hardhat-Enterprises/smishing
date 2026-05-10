package com.example.smishingdetectionapp

import android.app.Application
import com.example.smishingdetectionapp.di.initKoin

class SmishingDetectionApp : Application() {
    fun oncreate() {
        super.onCreate()
        // initialise Koin with the application context
        initKoin(this)
    }
}