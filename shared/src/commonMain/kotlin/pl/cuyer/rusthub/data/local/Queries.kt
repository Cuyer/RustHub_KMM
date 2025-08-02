package pl.cuyer.rusthub.data.local

import kotlinx.coroutines.CancellationException
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.util.CrashReporter

abstract class Queries(
    db: RustHubDatabase
) {
    val queries = db.rusthubDBQueries

    protected inline fun safeExecute(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
        }
    }

    protected inline fun <T> safeQuery(default: T, block: () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
            default
        }
    }
}