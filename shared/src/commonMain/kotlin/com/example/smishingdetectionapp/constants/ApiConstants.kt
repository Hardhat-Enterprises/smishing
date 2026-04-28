package com.example.smishingdetectionapp.constants

object ApiConstants {

    // Base URLs
    const val BASE_URL = ""
    const val OLLAMA_BASE_URL = "http://10.0.2.2:5000"
    const val RSS_FEED_URL = ""

    // Endpoints
    const val LOGIN_ENDPOINT = ""
    const val REGISTER_ENGPOINT = ""
    const val FAQ_ENDPOINT = ""
    const val COMMUNITY_ENDPOINT = ""
    const val NEWS_ENDPOINT = ""

    // Timeouts (in milliseconds)
    const val CONNECT_TIMEOUT = 60_000L
    const val READ_TIMEOUT = 90_000L
    const val WRITE_TIMEOUT = 60_000L

    // Retry
    const val MAX_RETRIES = 3
    const val RETRY_DELAY_MS = 1_000L

    // Ollama
    const val OLLAMA_MODEL = "llama3.2:1b"
    const val OLLAMA_TIMEOUT = 60_000L

    // Ollama Endpoints
    const val OLLAMA_CHAT_ENDPOINT = "/chat/api/generate"
}
