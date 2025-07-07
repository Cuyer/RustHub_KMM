package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NotificationInfoDialog(
    showDialog: Boolean,
    title: String = "Enable notifications",
    message: String = "Stay up to date with updates and news.",
    confirmButtonText: String = "Continue",
    dismissButtonText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = title) },
            text = { Text(text = message) },
            confirmButton = {
                AppButton(onClick = onConfirm) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                AppTextButton(onClick = onDismiss) {
                    Text(dismissButtonText)
                }
            }
        )
    }
}

