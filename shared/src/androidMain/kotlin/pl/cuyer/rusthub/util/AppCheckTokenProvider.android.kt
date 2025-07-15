package pl.cuyer.rusthub.util

import com.google.firebase.appcheck.FirebaseAppCheck
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await

actual class AppCheckTokenProvider actual constructor() {

    actual suspend fun currentToken(): String? {
        return try {
            FirebaseAppCheck.getInstance().getToken(false).await().token
        } catch (e: Exception) {
            Napier.e("Failed to get AppCheck token", e)
            null
        }
    }
}
