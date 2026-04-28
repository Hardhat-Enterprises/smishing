package com.example.smishingdetectionapp.data

import com.example.smishingdetectionapp.data.model.LoggedInUser
import com.example.smishingdetectionapp.util.generateUUID

class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val fakeUser = LoggedInUser(
                userId = generateUUID(),
                displayName = "Jane Doe"
            )
            Result.Success(fakeUser)
        } catch (e: Exception) {
            Result.Error(Exception("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    private var storedPin: String = "123456"

    fun verifyPin(pin: String): Boolean = storedPin == pin

    fun savePin(pin: String) {
        storedPin = pin
    }
}
