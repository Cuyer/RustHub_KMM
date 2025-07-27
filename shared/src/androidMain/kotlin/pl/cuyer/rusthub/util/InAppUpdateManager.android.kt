package pl.cuyer.rusthub.util

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

actual class InAppUpdateManager(
    context: Context,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider
) {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var currentActivity: ComponentActivity? = null
    private var launcher: ActivityResultLauncher<IntentSenderRequest>? = null
    private var listenerRegistered = false
    private var immediateInProgress = false

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            unregisterListener()
            scope.launch {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = stringProvider.get(SharedRes.strings.update_ready),
                        action = SnackbarAction(stringProvider.get(SharedRes.strings.restart)) {
                            appUpdateManager.completeUpdate()
                        },
                        duration = Duration.INDEFINITE
                    )
                )
            }
        }
    }

    fun setLauncher(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        activity: ComponentActivity
    ) {
        this.launcher = launcher
        this.currentActivity = activity
    }

    fun onUpdateResult(result: ActivityResult, activity: ComponentActivity) {
        if (result.resultCode != Activity.RESULT_OK) {
            Napier.d("In-app update flow failed: ${result.resultCode}")
            if (immediateInProgress) {
                activity.finishAffinity()
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
        currentActivity = act
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when {
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                    immediateInProgress = true
                    val options = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    launcher?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            it,
                            options
                        )
                    }
                }
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    immediateInProgress = false
                    registerListener()
                    val options = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    launcher?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            it,
                            options
                        )
                    }
                }
            }
        }
    }

    actual fun onResume(activity: Any) {
        val act = activity as? ComponentActivity ?: return
        currentActivity = act
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when {
                info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    immediateInProgress = true
                    val options = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    launcher?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            it,
                            options
                        )
                    }
                }
                info.installStatus() == InstallStatus.DOWNLOADED -> {
                    unregisterListener()
                    scope.launch {
                        snackbarController.sendEvent(
                            SnackbarEvent(
                                message = stringProvider.get(SharedRes.strings.update_ready),
                                action = SnackbarAction(stringProvider.get(SharedRes.strings.restart)) {
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