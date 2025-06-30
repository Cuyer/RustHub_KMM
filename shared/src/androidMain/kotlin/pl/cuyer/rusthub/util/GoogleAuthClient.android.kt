package pl.cuyer.rusthub.util

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import io.github.aakira.napier.Napier
import org.json.JSONObject

actual class GoogleAuthClient(private val context: Context) {
    actual suspend fun getIdToken(clientId: String): String? {
        val manager = CredentialManager.create(context)
        val signInOption = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setFilterByAuthorizedAccounts(true)
            .build()
        val signInRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        val result = try {
            manager.getCredential(context, signInRequest)
        } catch (noCred: NoCredentialException) {
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

        val credential = result.credential as? PublicKeyCredential ?: return null
        val json = JSONObject(credential.authenticationResponseJson)
        return json.getString("id_token")
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
