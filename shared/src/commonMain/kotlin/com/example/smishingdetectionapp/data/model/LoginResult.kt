package com.example.smishingdetectionapp.data.model

data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: String? = null
)
