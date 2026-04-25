package com.example.smishingdetectionapp.data

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String = when (this) {
        is Success -> "Success[data=$data]"
        is Error -> "Error[exception=$exception]"
    }
}
