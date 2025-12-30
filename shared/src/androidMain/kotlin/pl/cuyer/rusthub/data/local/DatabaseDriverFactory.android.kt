package pl.cuyer.rusthub.data.local

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Base64
import android.util.Log
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import database.FiltersEntity
import database.ItemEntity
import database.MonumentEntity
import database.ServerEntity
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.SharedBuildConfig

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
            itemEntityAdapter = ItemEntity.Adapter(
                languageAdapter = EnumColumnAdapter()
            ),
            monumentEntityAdapter = MonumentEntity.Adapter(
                languageAdapter = EnumColumnAdapter(),
            )
        )
    }

    private fun createDriver(): AndroidSqliteDriver {
        return if (SharedBuildConfig.USE_ENCRYPTED_DB) {
            loadSqlCipherLibraries()
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

    private fun loadSqlCipherLibraries() {
        if (sqlCipherLoaded) {
            return
        }

        synchronized(lock) {
            if (sqlCipherLoaded) {
                return
            }

            runCatching {
                System.loadLibrary("sqlcipher")
            }.onSuccess {
                sqlCipherLoaded = true
            }.onFailure { loadError ->
                Log.e(TAG, "Unable to load SQLCipher native library", loadError)
                throw loadError
            }
        }
    }

    private companion object {
        private const val DATABASE_NAME = "RustHubDatabase.db"
        private const val TAG = "DatabaseDriverFactory"
        private val lock = Any()
        @Volatile private var sqlCipherLoaded = false
    }
}
