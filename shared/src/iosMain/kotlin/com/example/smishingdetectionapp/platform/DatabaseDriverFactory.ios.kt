package com.example.smishingdetectionapp.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.smishingdetectionapp.SmishingDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SmishingDatabase.Schema,
            name = "smishing.db"
        )
    }
}