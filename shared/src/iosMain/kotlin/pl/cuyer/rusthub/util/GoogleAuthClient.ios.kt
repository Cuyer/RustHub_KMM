package pl.cuyer.rusthub.util

actual class GoogleAuthClient {
    actual suspend fun getIdToken(clientId: String): String? = null
    actual suspend fun signOut() {}
}
