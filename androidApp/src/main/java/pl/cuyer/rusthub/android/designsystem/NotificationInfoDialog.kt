package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.StringResource
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun NotificationInfoDialog(
    showDialog: Boolean,
    title: StringResource = SharedRes.strings.enable_notifications,
    message: StringResource = SharedRes.strings.stay_up_to_date_with_updates_and_news,
    confirmButtonText: StringResource = SharedRes.strings.continue_further,
    dismissButtonText: StringResource = SharedRes.strings.cancel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(title)) },
            text = { Text(text = stringResource(message)) },
            confirmButton = {
                AppButton(onClick = onConfirm) {
                    Text(stringResource(confirmButtonText))
                }
            },
            dismissButton = {
                AppTextButton(onClick = onDismiss) {
                    Text(stringResource(dismissButtonText))
                }
            }
        )
    }
}

