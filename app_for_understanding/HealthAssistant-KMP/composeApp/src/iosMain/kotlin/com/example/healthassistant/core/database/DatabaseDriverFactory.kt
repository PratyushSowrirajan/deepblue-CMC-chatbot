package com.example.healthassistant.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.healthassistant.db.HealthDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            HealthDatabase.Schema,
            "health.db"
        )
    }
}