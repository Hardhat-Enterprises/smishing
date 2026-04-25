package com.example.smishingdetectionapp.data

import com.example.smishingdetectionapp.data.model.LoggedInUser

class LoginRepository(private val dataSource: LoginDataSource) {

    private var user: LoggedInUser? = null

    val isLoggedIn: Boolean
        get() = user != null

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.login(username, password)
        if (result is Result.Success) {
            user = result.data
        }
        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        user = loggedInUser
    }

    companion object {
        private var instance: LoginRepository? = null

        fun getInstance(dataSource: LoginDataSource): LoginRepository {
            return instance ?: LoginRepository(dataSource).also { instance = it }
        }
    }
}
