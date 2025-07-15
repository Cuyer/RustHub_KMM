package pl.cuyer.rusthub.util

actual class BiometricAuthenticator {
    actual suspend fun authenticate(activity: Any): Boolean = true
}
