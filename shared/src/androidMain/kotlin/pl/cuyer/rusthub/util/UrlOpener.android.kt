package pl.cuyer.rusthub.util

import android.content.Intent
import androidx.core.net.toUri

actual class UrlOpener(private val activityProvider: ActivityProvider) {
    actual fun openUrl(url: String) {
        val context = activityProvider.currentActivity() ?: return
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}
