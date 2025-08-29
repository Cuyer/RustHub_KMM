package pl.cuyer.rusthub.util

import com.google.android.play.core.integrity.IntegrityServiceException
import com.google.android.play.core.integrity.model.IntegrityErrorCode
import com.google.firebase.appcheck.FirebaseAppCheck
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

actual class AppCheckTokenProvider actual constructor(
    private val stringProvider: StringProvider,
    private val snackbarController: SnackbarController,
) {

    actual suspend fun currentToken(): String? {
        var currentDelay = INITIAL_DELAY_MS
        repeat(MAX_RETRIES) { attempt ->
            try {
                return FirebaseAppCheck.getInstance()
                    .getAppCheckToken(false).await().token
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                if (notifyGoogleAppsRequired(e)) {
                    CrashReporter.recordException(e)
                    return null
                }

                if (!isRetryable(e) || attempt == MAX_RETRIES - 1) {
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

    private suspend fun notifyGoogleAppsRequired(e: Exception): Boolean {
        val integrityCause = e.cause as? IntegrityServiceException
        return when (integrityCause?.errorCode) {
            IntegrityErrorCode.PLAY_STORE_NOT_FOUND,
            IntegrityErrorCode.PLAY_STORE_VERSION_OUTDATED -> {
                withContext(Dispatchers.Main.immediate) {
                    snackbarController.sendEvent(
                        SnackbarEvent(
                            stringProvider.get(SharedRes.strings.play_store_required)
                        )
                    )
                }
                true
            }
            IntegrityErrorCode.PLAY_STORE_ACCOUNT_NOT_FOUND -> {
                withContext(Dispatchers.Main.immediate) {
                    snackbarController.sendEvent(
                        SnackbarEvent(
                            stringProvider.get(SharedRes.strings.play_store_sign_in_required)
                        )
                    )
                }
                true
            }
            IntegrityErrorCode.PLAY_SERVICES_NOT_FOUND,
            IntegrityErrorCode.PLAY_SERVICES_VERSION_OUTDATED -> {
                withContext(Dispatchers.Main.immediate) {
                    snackbarController.sendEvent(
                        SnackbarEvent(
                            stringProvider.get(SharedRes.strings.play_services_required)
                        )
                    )
                }
                true
            }
            else -> false
        }
    }

    private fun isRetryable(e: Exception): Boolean {
        val integrityCause = e.cause as? IntegrityServiceException
        return integrityCause?.errorCode in listOf(
            IntegrityErrorCode.NETWORK_ERROR,
            IntegrityErrorCode.TOO_MANY_REQUESTS,
            IntegrityErrorCode.GOOGLE_SERVER_UNAVAILABLE,
            IntegrityErrorCode.CLIENT_TRANSIENT_ERROR,
            IntegrityErrorCode.INTERNAL_ERROR
        )
    }

    private companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 1000L
    }
}
