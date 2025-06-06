package pl.cuyer.rusthub.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import pl.cuyer.rusthub.database.PigeonTreeDatabase

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver {
        return NativeSqliteDriver(
            schema = ColorfulDatabase.Schema,
            name = "ColorfulDatabase.db"
        )
    }
}