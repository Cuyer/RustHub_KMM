package pl.cuyer.rusthub.util

actual class AppCheckTokenProvider actual constructor() {
    actual suspend fun currentToken(): String? = null
}
