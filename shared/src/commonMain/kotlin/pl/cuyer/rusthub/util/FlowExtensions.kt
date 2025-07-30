package pl.cuyer.rusthub.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.CancellationException

fun <T> Flow<T>.catchAndLog(action: suspend (Throwable) -> Unit = {}): Flow<T> =
    catch { e ->
        if (e is CancellationException) throw e
        CrashReporter.recordException(e)
        action(e)
    }
