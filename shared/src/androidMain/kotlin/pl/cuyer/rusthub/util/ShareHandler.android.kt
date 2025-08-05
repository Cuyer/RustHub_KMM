package pl.cuyer.rusthub.util

import android.content.Intent

actual class ShareHandler(private val activityProvider: ActivityProvider) {
    actual fun share(text: String) {
        val context = activityProvider.currentActivity() ?: return
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(sendIntent, null)
        context.startActivity(chooser)
    }
}
