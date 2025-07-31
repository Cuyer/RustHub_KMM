package pl.cuyer.rusthub.util

import com.google.firebase.appcheck.FirebaseAppCheck
import io.github.aakira.napier.Napier
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

actual class AppCheckTokenProvider actual constructor() {

    actual suspend fun currentToken(): String? {
        return try {
            val token = FirebaseAppCheck.getInstance().getToken(false).await().token
            token
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
            null
        }
    }
}
