package pl.cuyer.rusthub.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.catchAndLog(action: suspend (Throwable) -> Unit = {}): Flow<T> =
    catch { e ->
        CrashReporter.recordException(e)
        action(e)
    }
