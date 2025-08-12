package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.common.Result

expect class GoogleAuthClient {
    suspend fun getIdToken(clientId: String): Result<String>
    suspend fun signOut(): Result<Unit>
}
