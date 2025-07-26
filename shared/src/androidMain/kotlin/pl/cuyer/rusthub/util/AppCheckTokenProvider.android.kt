package pl.cuyer.rusthub.util

import com.google.firebase.appcheck.FirebaseAppCheck
import io.github.aakira.napier.Napier
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.coroutines.tasks.await

actual class AppCheckTokenProvider actual constructor() {

    actual suspend fun currentToken(): String? {
        CrashReporter.log("Requesting AppCheck token")
        return try {
            Napier.d("Requesting AppCheck token", tag = "AppCheck")
            val token = FirebaseAppCheck.getInstance().getToken(false).await().token
            CrashReporter.log("AppCheck token acquired")
            Napier.d("AppCheck token acquired", tag = "AppCheck")
            token
        } catch (e: Exception) {
            CrashReporter.recordException(e)
            CrashReporter.log("Failed to get AppCheck token: ${e.message}")
            Napier.e(message = "Failed to get AppCheck token", throwable = e, tag = "AppCheck")
            null
        }
    }
}
