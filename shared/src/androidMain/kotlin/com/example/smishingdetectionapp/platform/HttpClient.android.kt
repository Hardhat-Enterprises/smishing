package com.example.smishingdetectionapp.platform

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

actual fun createPlatformHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        engine {

            config {
                //OkHttp config goes in here.

            }
        }
    }
}