package pl.cuyer.rusthub.util

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

actual class GoogleAuthClient(private val activityProvider: ActivityProvider) {

    private val mutex = Mutex()

    actual suspend fun getIdToken(clientId: String): String? = mutex.withLock {
        val activity = activityProvider.currentActivity() ?: return null

        // Wait for a RESUMED host if possible (AppCompatActivity implements LifecycleOwner)
        waitForResumedIfPossible(activity)

        val cm = CredentialManager.create(activity)

        val authorizedAutoSelect = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)               // <-- Web client ID
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(true)                 // instant if exactly one authorized account
            .build()

        val anyAccountChooser = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setFilterByAuthorizedAccounts(false)       // allow any signed-in Google account
            .setAutoSelectEnabled(false)                // force chooser; avoids wrong auto-pick
            .build()

        // Pre-warm: opportunistic, no UI. Safe on API 30–33 (and up). Ignore any result.
        runCatching {
            val opportunistic = GetCredentialRequest.Builder()
                .addCredentialOption(anyAccountChooser)
                .setPreferImmediatelyAvailableCredentials(true) // returns immediately if none cached
                .build()
            cm.getCredential(activity, opportunistic)
        }

        // Try authorized + autoselect first (fast path)
        val primary = runCatching {
            cm.getCredential(
                activity,
                GetCredentialRequest.Builder()
                    .addCredentialOption(authorizedAutoSelect)
                    .build()
            )
          }.getOrElse { e ->
              if (e is CancellationException) throw e
              if (e is GetCredentialCancellationException) return null
              if (e !is NoCredentialException) CrashReporter.recordException(e)
              null
          }

        // Fallback: show chooser for any account
        val result = primary ?: runCatching {
            cm.getCredential(
                activity,
                GetCredentialRequest.Builder()
                    .addCredentialOption(anyAccountChooser)
                    .build()
            )
          }.getOrElse { e ->
              if (e is CancellationException) throw e
              if (e is GetCredentialCancellationException) return null
              CrashReporter.recordException(e)
              return null
          }

        val cred = result.credential
        if (cred is CustomCredential &&
            cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return try {
                GoogleIdTokenCredential.createFrom(cred.data).idToken
            } catch (e: GoogleIdTokenParsingException) {
                CrashReporter.recordException(e)
                null
            }
        }
        null
    }

    actual suspend fun signOut() {
        val context = activityProvider.currentActivity() ?: return
        try {
            CredentialManager.create(context)
                .clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
private suspend fun waitForResumedIfPossible(activity: Activity) {
    val owner = activity as? LifecycleOwner ?: return
    val lifecycle = owner.lifecycle
    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return

    suspendCancellableCoroutine { cont ->
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    source.lifecycle.removeObserver(this)
                    cont.tryResume(Unit)?.let { token ->
                        cont.completeResume(token)
                    }
                }
            }
        }
        lifecycle.addObserver(observer)
        cont.invokeOnCancellation { lifecycle.removeObserver(observer) }
    }
}