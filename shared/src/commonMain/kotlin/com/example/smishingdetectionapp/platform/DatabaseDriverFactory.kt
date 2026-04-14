package com.example.smishingdetectionapp.platform

import app.cash.sqldelight.db.SqlDriver

// platform creates the driver differently
// Android needs context, iOS does not
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}