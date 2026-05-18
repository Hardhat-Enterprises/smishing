package com.example.smishingdetectionapp.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.smishingdetectionapp.SmishingDatabase

actual class DatabaseDriverFactory (private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SmishingDatabase.Schema,
            context = context,
            name = "smishing.db"
        )
    }
}