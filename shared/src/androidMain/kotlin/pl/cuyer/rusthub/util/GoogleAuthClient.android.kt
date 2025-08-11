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
        val signInOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()
        val signInRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        val signUpOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .build()
        val signUpRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signUpOption)
            .build()

        val result = try {
            manager.getCredential(activity, signInRequest)
        } catch (noCred: NoCredentialException) {
            try {
                manager.getCredential(activity, signUpRequest)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                CrashReporter.recordException(e)
                return null
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
            return null
        }

        return handleSignIn(result.credential)
    }

    private fun handleSignIn(credential: Credential): String? {
        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    return try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        googleIdTokenCredential.idToken
                    } catch (e: GoogleIdTokenParsingException) {
                        CrashReporter.recordException(e)
                        null
                    }
                } else null
            }

            else -> null
        }
    }

    actual suspend fun signOut() {
        val context = activityProvider.currentActivity() ?: return
        try {
            CredentialManager.create(context)
                .clearCredentialState(
                    ClearCredentialStateRequest(),
                )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
        }
    }
}
