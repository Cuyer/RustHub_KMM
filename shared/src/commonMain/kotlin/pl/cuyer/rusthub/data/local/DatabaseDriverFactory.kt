package pl.cuyer.rusthub.data.local

import pl.cuyer.rusthub.database.RustHubDatabase

expect class DatabaseDriverFactory {
    suspend fun create(): RustHubDatabase
}