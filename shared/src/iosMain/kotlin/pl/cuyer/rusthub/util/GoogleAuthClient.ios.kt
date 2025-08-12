package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.common.Result

actual class GoogleAuthClient {
    actual suspend fun getIdToken(clientId: String): Result<String> =
        Result.Error(Exception("Google sign-in not supported"))

    actual suspend fun signOut(): Result<Unit> = Result.Success(Unit)
}
