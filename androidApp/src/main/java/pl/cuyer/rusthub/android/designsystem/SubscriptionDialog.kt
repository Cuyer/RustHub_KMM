package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.StringResource
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun SubscriptionDialog(
    showDialog: Boolean,
    title: StringResource = SharedRes.strings.subscribe_now,
    message: StringResource = SharedRes.strings.get_unlimited_access_to_premium_features,
    confirmButtonText: StringResource = SharedRes.strings.subscribe,
    dismissButtonText: StringResource = SharedRes.strings.later,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = stringResource(title))
            },
            text = {
                Text(text = stringResource(message))
            },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text(stringResource(confirmButtonText))
                }
            },
            dismissButton = {
                AppButton(onClick = onDismiss) {
                    Text(stringResource(dismissButtonText))
                }
            }
        )
    }
}