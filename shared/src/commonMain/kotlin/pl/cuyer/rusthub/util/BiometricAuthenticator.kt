package pl.cuyer.rusthub.util

expect class BiometricAuthenticator {
    suspend fun authenticate(activity: Any): Boolean
}
