package pl.cuyer.rusthub.android.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@DslMarker
annotation class PermissionsDSL

@PermissionsDSL
class PermissionHandlerScope(
    val isGranted: Boolean,
    val shouldShowRationale: Boolean,
    private val launchRequest: () -> Unit
) {
    private var handled = false

    fun launchPermissionRequest() {
        launchRequest()
    }

    @Composable
    fun onGranted(body: @Composable () -> Unit) {
        if (!handled && isGranted) {
            handled = true
            body()
        }
    }

    @Composable
    fun onShowRationale(body: @Composable (PermissionHandlerScope) -> Unit) {
        if (!handled && shouldShowRationale && !isGranted) {
            handled = true
            body(this)
        }
    }

    @Composable
    fun onRequestPermission(body: @Composable () -> Unit) {
        if (!handled && !isGranted && !shouldShowRationale) {
            handled = true
            body()
            launchRequest()
        }
    }

    @Composable
    fun onDenied(body: @Composable () -> Unit) {
        if (!handled && !isGranted) {
            handled = true
            body()
        }
    }
}

@Composable
fun HandlePermission(
    permission: String,
    onResult: (Boolean) -> Unit,
    content: @Composable PermissionHandlerScope.() -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult
    )

    val granted = ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    val rationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
    } ?: false

    PermissionHandlerScope(
        isGranted = granted,
        shouldShowRationale = rationale,
        launchRequest = { launcher.launch(permission) }
    ).content()
}
