package com.example.smishingdetectionapp.platform

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// platform provides its own engine
expect fun createPlatformHttpClient(): HttpClient

//shared configuration that wraps the platform engine
fun createHttpClient(): HttpClient {
    return createPlatformHttpClient().config {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true    // stops crash if API adds new fields
                isLenient = true            // tolerates slightly malformed JSON
                prettyPrint = false
            })
        }

        //shared plugins can be added here:
        // - logging
        // - default request headers
        // - timeouts

    }
}