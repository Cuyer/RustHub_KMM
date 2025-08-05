package pl.cuyer.rusthub.data.local

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Base64
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import database.FiltersEntity
import database.ServerEntity
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.BuildConfig

actual class DatabaseDriverFactory(
    private val context: Context,
    private val passphraseProvider: DatabasePassphraseProvider? = null
) {
    actual suspend fun create(): RustHubDatabase {
        val passphrase = passphraseProvider?.getPassphrase()
        val driver = try {
            createDriver(passphrase)
        } catch (e: SQLiteException) {
            context.deleteDatabase(DATABASE_NAME)
            createDriver(passphrase)
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

    private fun createDriver(passphrase: String?): AndroidSqliteDriver {
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
