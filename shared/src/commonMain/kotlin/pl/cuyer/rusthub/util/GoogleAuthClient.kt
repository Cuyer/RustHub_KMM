package pl.cuyer.rusthub.util

expect class GoogleAuthClient {
    suspend fun getIdToken(clientId: String): String?
    suspend fun signOut()
}
