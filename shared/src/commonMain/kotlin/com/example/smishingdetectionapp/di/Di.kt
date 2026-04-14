package com.example.smishingdetectionapp.di

import org.koin.core.module.Module
import org.koin.dsl.module

// shared, platform-agnostic bindings (repositories, services, etc.)
val sharedModule: Module = module {
    // Exmaple:
    // single<ChatRepository> { ChatRepositoryImpl(get)) }
}

// Platform module is provided by platform
expect fun platformModule(): Module

// Init is platform-specifric and will call startKoin on Android/iOS respectively
expect fun initKoin(context: Any? = null)
