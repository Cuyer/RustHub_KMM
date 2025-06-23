package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SubscriptionDialog(
    showDialog: Boolean,
    title: String = "Subscribe now",
    message: String = "Get unlimited access to premium features.",
    confirmButtonText: String = "Subscribe",
    dismissButtonText: String = "Later",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                AppButton(onClick = onDismiss) {
                    Text(dismissButtonText)
                }
            }
        )
    }
}