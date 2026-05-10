package com.example.smishingdetectionapp.di

import android.content.Context
import org.koin.core.module.Module
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

actual fun platformModule(): Module {
    // example: Android DB driver factory that needs Context:
    // single { DatabaseDriverFactory(androidContext()) }
    return TODO("Provide the return value")
}

// Initialise Koin on Android (pass Application context)
actual fun initKoin(context: Any?) {
    val appContext = context as? Context
        ?: throw IllegalArgumentException("Android initKoin requires a Context")
    startKoin {
        androidContext(appContext)
        modules(listOf(sharedModule, platformModule()))
    }
}

