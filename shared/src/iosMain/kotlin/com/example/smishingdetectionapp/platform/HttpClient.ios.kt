package com.example.smishingdetectionapp.platform

import io.ktor.client.*
import io.ktor.client.engine.darwin.*

actual fun createPlatformHttpClient(): HttpClient {
    return HttpClient(Darwin) {
        engine {

            configureRequest {
                //Darwin config goes in here.
            }
        }
    }
}