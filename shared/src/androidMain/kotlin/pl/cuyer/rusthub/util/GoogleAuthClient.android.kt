package pl.cuyer.rusthub.util

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CancellationException

actual class GoogleAuthClient(
    private val activityProvider: ActivityProvider,
    private val manager: CredentialManager
) {
    actual suspend fun getIdToken(clientId: String): String? {
        val activity = activityProvider.currentActivity() ?: return null
        val signIn = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(true)
            .build()

        val signUp = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val trySignIn = GetCredentialRequest.Builder().addCredentialOption(signIn).build()
        val trySignUp = GetCredentialRequest.Builder().addCredentialOption(signUp).build()

        val response = try {
            manager.getCredential(activity, trySignIn)
        } catch (e: NoCredentialException) {
            CrashReporter.recordException(e)
            manager.getCredential(activity, trySignUp)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashReporter.recordException(e)
            return null
        }

        return handleSignIn(response.credential)
    }

    private fun handleSignIn(credential: Credential): String? {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return try {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } catch (e: GoogleIdTokenParsingException) {
                CrashReporter.recordException(e); null
            }
        }
        return null
    }

    actual suspend fun signOut() {
        try {
            manager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashReporter.recordException(e)
        }
    }
}
