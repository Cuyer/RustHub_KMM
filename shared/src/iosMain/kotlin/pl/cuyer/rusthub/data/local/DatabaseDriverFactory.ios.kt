package pl.cuyer.rusthub.data.local

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import database.Server
import pl.cuyer.rusthub.database.RustHubDatabase

actual class DatabaseDriverFactory {
    actual fun create(): RustHubDatabase {
        val driver = NativeSqliteDriver(
            schema = RustHubDatabase.Schema,
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