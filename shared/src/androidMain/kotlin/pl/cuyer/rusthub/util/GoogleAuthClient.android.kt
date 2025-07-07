package pl.cuyer.rusthub.util

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.aakira.napier.Napier

actual class GoogleAuthClient(private val context: Context) {
    actual suspend fun getIdToken(clientId: String): String? {
        val manager = CredentialManager.create(context)
        val signInOption = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .setFilterByAuthorizedAccounts(true)
            .build()
        val signInRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        val result = try {
            manager.getCredential(context, signInRequest)
        } catch (noCred: NoCredentialException) {
            try {
                val signUpOption = GetGoogleIdOption.Builder()
                    .setServerClientId(clientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
                val signUpRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(signUpOption)
                    .build()
                manager.getCredential(context, signUpRequest)
            } catch (e: Exception) {
                Napier.e("Google sign in failed", e)
                return null
            }
        } catch (e: Exception) {
            Napier.e("Google sign in failed", e)
            return null
        }

        val credential = result.credential

        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    return try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        googleIdTokenCredential.idToken
                    } catch (e: GoogleIdTokenParsingException) {
                        Napier.e("Google sign in failed", e)
                        null
                    }
                } else null
            }

            else -> null
        }
    }

    actual suspend fun signOut() {
        try {
            CredentialManager.create(context)
                .clearCredentialState(
                    ClearCredentialStateRequest()
                )
        } catch (e: Exception) {
            Napier.e("Google sign out failed", e)
        }
    }
}
