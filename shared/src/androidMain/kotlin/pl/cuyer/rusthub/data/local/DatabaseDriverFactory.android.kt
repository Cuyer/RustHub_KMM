package pl.cuyer.rusthub.data.local

import android.content.Context
import android.util.Base64
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import database.FiltersEntity
import database.ServerEntity
import net.zetetic.database.sqlcipher.SQLiteException
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.BuildConfig

actual class DatabaseDriverFactory(
    private val context: Context,
    private val passphrase: String? = null
) {
    actual fun create(): RustHubDatabase {
        val driver = try {
            createDriver()
        } catch (e: SQLiteException) {
            context.deleteDatabase(DATABASE_NAME)
            createDriver()
        }

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
        )
    }

    private fun createDriver(): AndroidSqliteDriver {
        return if (BuildConfig.USE_ENCRYPTED_DB) {
            System.loadLibrary("sqlcipher")
            val factory = SupportOpenHelperFactory(
                Base64.decode(requireNotNull(passphrase), Base64.NO_WRAP)
            )
            AndroidSqliteDriver(
                schema = RustHubDatabase.Schema,
                context = context,
                name = DATABASE_NAME,
                factory = factory
            )
        } else {
            AndroidSqliteDriver(
                schema = RustHubDatabase.Schema,
                context = context,
                name = DATABASE_NAME
            )
        }
    }

    private companion object {
        private const val DATABASE_NAME = "RustHubDatabase.db"
    }
}
