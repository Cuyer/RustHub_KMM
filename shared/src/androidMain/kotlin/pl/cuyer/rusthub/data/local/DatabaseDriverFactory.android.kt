package pl.cuyer.rusthub.data.local

import android.content.Context
import android.util.Base64
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import database.FiltersEntity
import database.ServerEntity
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import pl.cuyer.rusthub.database.RustHubDatabase

actual class DatabaseDriverFactory(
    private val context: Context,
    private val passphrase: String
) {
    actual fun create(): RustHubDatabase {
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(Base64.decode(passphrase, Base64.NO_WRAP))
        val driver = AndroidSqliteDriver(
            schema = RustHubDatabase.Schema,
            context = context,
            name = "RustHubDatabase.db",
            factory = factory
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
                sort_orderAdapter = EnumColumnAdapter(),
                filterAdapter = EnumColumnAdapter()
            ),
            settingsEntityAdapter = database.SettingsEntity.Adapter(
                themeAdapter = EnumColumnAdapter(),
                languageAdapter = EnumColumnAdapter()
            )
        )
    }
}