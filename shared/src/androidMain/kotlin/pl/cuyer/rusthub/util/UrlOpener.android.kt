package pl.cuyer.rusthub.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

actual class UrlOpener(private val context: Context) {
    actual fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
