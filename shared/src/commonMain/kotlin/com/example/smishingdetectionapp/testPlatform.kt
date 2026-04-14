package com.example.smishingdetectionapp

class testPlatform {
    private val platform = Platform()

    fun outputPlatform(): String {
        return "This is outputing from the shared module for the ${platform.name} platform."
    }
}