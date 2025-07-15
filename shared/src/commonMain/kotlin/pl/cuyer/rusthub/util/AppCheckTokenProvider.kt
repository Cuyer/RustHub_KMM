package pl.cuyer.rusthub.util

/** Provides Firebase App Check tokens. */
expect class AppCheckTokenProvider() {
    suspend fun currentToken(): String?
}
