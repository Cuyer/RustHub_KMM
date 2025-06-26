package pl.cuyer.rusthub.data.local

import android.content.Context
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import database.FiltersEntity
import database.ServerEntity
import pl.cuyer.rusthub.database.RustHubDatabase

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
            serverEntityAdapter = ServerEntity.Adapter(
                map_nameAdapter = EnumColumnAdapter(),
                server_flagAdapter = EnumColumnAdapter(),
                regionAdapter = EnumColumnAdapter(),
                difficultyAdapter = EnumColumnAdapter(),
                wipe_scheduleAdapter = EnumColumnAdapter(),
                wipe_typeAdapter = EnumColumnAdapter(),
                server_statusAdapter = EnumColumnAdapter()
            ),
            filtersEntityAdapter = FiltersEntity.Adapter(
                map_nameAdapter = EnumColumnAdapter(),
                server_flagAdapter = EnumColumnAdapter(),
                regionAdapter = EnumColumnAdapter(),
                difficultyAdapter = EnumColumnAdapter(),
                wipe_scheduleAdapter = EnumColumnAdapter(),
                sort_orderAdapter = EnumColumnAdapter()
            ),
            settingsEntityAdapter = database.SettingsEntity.Adapter(
                themeAdapter = EnumColumnAdapter(),
                languageAdapter = EnumColumnAdapter()
            )
        )
    }
}