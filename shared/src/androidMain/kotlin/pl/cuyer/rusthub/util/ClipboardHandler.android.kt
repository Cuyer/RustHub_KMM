package pl.cuyer.rusthub.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService

actual class ClipboardHandler(private val context: Context) {
    actual fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService<ClipboardManager>()
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
    }
}