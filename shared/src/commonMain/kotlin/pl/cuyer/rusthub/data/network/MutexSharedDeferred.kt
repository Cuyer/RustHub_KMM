package pl.cuyer.rusthub.data.network

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class MutexSharedDeferred<T> {
    private val mutex = Mutex()
    private var deferred: Deferred<T>? = null

    suspend fun run(block: suspend () -> T): T {
        val current = mutex.withLock {
            deferred?.takeIf { it.isActive } ?: coroutineScope {
                async { block() }.also { deferred = it }
            }
        }
        return try {
            current.await()
        } finally {
            mutex.withLock {
                if (deferred === current) {
                    deferred = null
                }
            }
        }
    }
}
