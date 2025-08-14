package pl.cuyer.rusthub.util

import com.google.android.play.core.integrity.IntegrityErrorCode
import com.google.android.play.core.integrity.IntegrityServiceException
import com.google.firebase.appcheck.FirebaseAppCheck
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import pl.cuyer.rusthub.util.CrashReporter

actual class AppCheckTokenProvider actual constructor() {

    actual suspend fun currentToken(): String? {
        var currentDelay = INITIAL_DELAY_MS
        repeat(MAX_RETRIES) { attempt ->
            try {
                return FirebaseAppCheck.getInstance()
                    .getAppCheckToken(false).await().token
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                if (!isTransient(e) || attempt == MAX_RETRIES - 1) {
                    CrashReporter.recordException(e)
                    return null
                }

                Napier.w(
                    message =
                        "Transient Play Integrity error while fetching App Check token, retrying in $currentDelay ms",
                    throwable = e
                )
                delay(currentDelay)
                currentDelay *= 2
            }
        }
        return null
    }

    private fun isTransient(e: Exception): Boolean {
        val integrityCause = e.cause as? IntegrityServiceException
        return integrityCause?.errorCode == IntegrityErrorCode.CLIENT_TRANSIENT_ERROR
    }

    private companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 200L
    }
}
