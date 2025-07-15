package pl.cuyer.rusthub.util

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import io.github.aakira.napier.Napier
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.ktx.startUpdateFlowForResult
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

actual class InAppUpdateManager actual constructor(
    private val context: Context,
    private val snackbarController: SnackbarController
) {
    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var currentActivity: ComponentActivity? = null
    private var immediateInProgress = false

    private var launcher: ActivityResultLauncher<IntentSenderRequest>? = null
    private var listenerRegistered = false

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            unregisterListener()
            scope.launch {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = "Update ready to install",
                        action = SnackbarAction("Restart") { appUpdateManager.completeUpdate() },
                        duration = Duration.INDEFINITE
                    )
                )
            }
        }
    }

    private fun ensureLauncher(activity: ComponentActivity) {
        currentActivity = activity
        if (launcher == null) {
            launcher = activity.registerForActivityResult(StartIntentSenderForResult()) { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    Napier.d("In-app update flow failed: ${'$'}{result.resultCode}")
                    if (immediateInProgress) {
                        currentActivity?.finishAffinity()
                    }
                }
            }
        }
    }

    private fun registerListener() {
        if (!listenerRegistered) {
            appUpdateManager.registerListener(listener)
            listenerRegistered = true
        }
    }

    private fun unregisterListener() {
        if (listenerRegistered) {
            appUpdateManager.unregisterListener(listener)
            listenerRegistered = false
        }
    }

    actual fun check(activity: Any) {
        val act = activity as? ComponentActivity ?: return
        ensureLauncher(act)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when {
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                    immediateInProgress = true
                    launcher?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            AppUpdateType.IMMEDIATE,
                            it
                        )
                    }
                }
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    immediateInProgress = false
                    registerListener()
                    launcher?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            AppUpdateType.FLEXIBLE,
                            it
                        )
                    }
                }
            }
        }
    }

    actual fun onResume(activity: Any) {
        val act = activity as? ComponentActivity ?: return
        ensureLauncher(act)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when {
                info.updateAvailability() ==
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    immediateInProgress = true
                    launcher?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            AppUpdateType.IMMEDIATE,
                            it
                        )
                    }
                }
                info.installStatus() == InstallStatus.DOWNLOADED -> {
                    unregisterListener()
                    scope.launch {
                        snackbarController.sendEvent(
                            SnackbarEvent(
                                message = "Update ready to install",
                                action = SnackbarAction("Restart") {
                                    appUpdateManager.completeUpdate()
                                },
                                duration = Duration.INDEFINITE
                            )
                        )
                    }
                }
                info.installStatus() != InstallStatus.DOWNLOADED &&
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    immediateInProgress = false
                    registerListener()
                }
            }
        }
    }

}
