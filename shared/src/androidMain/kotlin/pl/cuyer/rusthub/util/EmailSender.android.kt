package pl.cuyer.rusthub.util

import android.content.Intent
import android.net.Uri

actual class EmailSender(private val activityProvider: ActivityProvider) {
    actual fun sendEmail(recipient: String, subject: String) {
        val context = activityProvider.currentActivity() ?: return
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        val chooser = Intent.createChooser(intent, null)
        context.startActivity(chooser)
    }
}
