package pl.cuyer.rusthub.data.local

import app.cash.sqldelight.db.SqlDriver
import pl.cuyer.rusthub.database.RustHubDatabase

expect class DatabaseDriverFactory {
    fun create(): RustHubDatabase
}