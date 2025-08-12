package pl.cuyer.rusthub.util

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.cuyer.rusthub.common.Result
import kotlin.coroutines.resume

actual class GoogleAuthClient(
    private val activityProvider: ActivityProvider,
    private val manager: CredentialManager
) {
    actual suspend fun getIdToken(clientId: String): Result<String> {
        val activity = activityProvider.currentActivity()
            ?: return Result.Error(IllegalStateException("No activity"))

        var nonce = UUID.randomUUID().toString()
        var credentialOption: CredentialOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .setNonce(nonce)
            .build()

        var request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(credentialOption)
            .build()

        return try {
            val result = manager.getCredential(activity, request)
            handleSignIn(result.credential)?.let { Result.Success(it) }
                ?: Result.Error(Exception("No Google credential"))
        } catch (e: GetCredentialException) {
            if (e.type == android.credentials.GetCredentialException.TYPE_USER_CANCELED) {
                return Result.Error(e)
            }
            try {
                nonce = UUID.randomUUID().toString()
                credentialOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(clientId)
                    .setAutoSelectEnabled(false)
                    .setNonce(nonce)
                    .build()

                if (e.type == android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL) {
                    credentialOption = GetSignInWithGoogleOption.Builder(clientId)
                        .setNonce(nonce)
                        .build()
                }

                request = GetCredentialRequest.Builder()
                    .addCredentialOption(credentialOption)
                    .build()

                val retry = manager.getCredential(activity, request)
                handleSignIn(retry.credential)?.let { Result.Success(it) }
                    ?: Result.Error(Exception("No Google credential"))
            } catch (e1: CancellationException) {
                throw e1
            } catch (e1: GetCredentialException) {
                handleFailure(activity, clientId, e1)
            } catch (e1: GetCredentialProviderConfigurationException) {
                handleFailure(activity, clientId, e1)
            } catch (e1: Exception) {
                CrashReporter.recordException(e1)
                Result.Error(e1)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashReporter.recordException(e)
            Result.Error(e)
        }
    }

    private suspend fun handleFailure(
        activity: Activity,
        clientId: String,
        e: Exception
    ): Result<String> {
        if (e is GetCredentialException &&
            e.type == android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL &&
            e.errorMessage?.lowercase()?.contains("28434") == true
        ) {
            return signInLegacy(activity, clientId)
        }
        CrashReporter.recordException(e)
        return Result.Error(e)
    }

    private suspend fun signInLegacy(
        activity: Activity,
        clientId: String
    ): Result<String> = suspendCancellableCoroutine { cont ->
        val registryOwner = activity as? ActivityResultRegistryOwner
            ?: run {
                cont.resume(Result.Error(IllegalStateException("No ActivityResultRegistry")))
                return@suspendCancellableCoroutine
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(activity, gso)
        val key = "google_sign_in_${UUID.randomUUID()}"
        val launcher: ActivityResultLauncher<Intent>
        launcher = registryOwner.activityResultRegistry.register(
            key,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val token = account.idToken
                if (token != null) {
                    cont.resume(Result.Success(token))
                } else {
                    cont.resume(Result.Error(Exception("No idToken")))
                }
            } catch (e: ApiException) {
                cont.resume(Result.Error(e))
            } catch (e: Exception) {
                cont.resume(Result.Error(e))
            } finally {
                launcher.unregister()
            }
        }

        launcher.launch(signInClient.signInIntent)
        cont.invokeOnCancellation { launcher.unregister() }
    }

    private fun handleSignIn(credential: Credential): String? {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return try {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } catch (e: GoogleIdTokenParsingException) {
                CrashReporter.recordException(e)
                null
            }
        }
        return null
    }

    actual suspend fun signOut(): Result<Unit> {
        return try {
            manager.clearCredentialState(ClearCredentialStateRequest())
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashReporter.recordException(e)
            Result.Error(e)
        }
    }
}

