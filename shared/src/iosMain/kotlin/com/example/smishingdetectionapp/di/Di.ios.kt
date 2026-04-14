package com.example.smishingdetectionapp.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

actual fun platformModule(): Module {
    // iOS specific bindings (no context)
    return TODO("Provide the return value")
}

// Initialise Koin on iOS
actual fun initKoin(context: Any?) {
    //no context needed
    startKoin {
        modules(listOf(sharedModule, platformModule()))
    }
}

