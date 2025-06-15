package pl.cuyer.rusthub.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import pl.cuyer.rusthub.database.RustHubDatabase
import app.cash.sqldelight.EnumColumnAdapter
import database.Server

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun create(): RustHubDatabase {
        val driver = AndroidSqliteDriver(
            schema = RustHubDatabase.Schema,
            context = context,
            name = "RustHubDatabase.db"
        )

        return RustHubDatabase.Companion(
            driver = driver,
            serverAdapter = Server.Adapter(
                map_nameAdapter = EnumColumnAdapter(),
                server_flagAdapter = EnumColumnAdapter(),
                regionAdapter = EnumColumnAdapter(),
                difficultyAdapter = EnumColumnAdapter(),
                wipe_scheduleAdapter = EnumColumnAdapter()
            )
        )
    }
}