package pl.cuyer.rusthub.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import pl.cuyer.rusthub.database.RustHubDatabase

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun create(): SqlDriver {
        return AndroidSqliteDriver(
            schema = RustHubDatabase.Schema,
            context = context,
            name = "RustHubDatabase.db"
        )
    }
}