package pl.cuyer.rusthub.util

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetGoogleIdOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.provider.PublicKeyCredential
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import com.google.android.gms.auth.api.identity.Identity

actual class GoogleAuthClient(private val context: Context) {
    actual suspend fun getIdToken(clientId: String): String? {
        val manager = CredentialManager.create(context)
        val signInOption = GetGoogleIdOption.Builder(clientId)
            .setFilterByAuthorizedAccounts(true)
            .build()
        val signInRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        val result = try {
            manager.getCredential(context, signInRequest)
        } catch (noCred: NoCredentialException) {
            val signUpOption = GetGoogleIdOption.Builder(clientId)
                .setFilterByAuthorizedAccounts(false)
                .build()
            val signUpRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signUpOption)
                .build()
            manager.getCredential(context, signUpRequest)
        } catch (e: GetCredentialException) {
            Napier.e("Google sign in failed", e)
            return null
        }

        val credential = result.credential as? PublicKeyCredential ?: return null
        val json = JSONObject(credential.authenticationResponseJson)
        return json.getString("id_token")
    }

    actual suspend fun signOut() {
        try {
            Identity.getSignInClient(context).signOut().await()
        } catch (e: Exception) {
            Napier.e("Google sign out failed", e)
        }
    }
}
