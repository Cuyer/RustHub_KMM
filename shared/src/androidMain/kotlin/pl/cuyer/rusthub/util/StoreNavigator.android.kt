package pl.cuyer.rusthub.util

import android.content.Intent
import androidx.core.net.toUri

actual class StoreNavigator(private val activityProvider: ActivityProvider) {
    actual fun openStore() {
        val context = activityProvider.currentActivity() ?: return
        val uri = "market://details?id=${context.packageName}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
            )
            context.startActivity(webIntent)
        }
    }

    actual fun openSubscriptionManagement(productId: String) {
        val context = activityProvider.currentActivity() ?: return
        val uri =
            "https://play.google.com/store/account/subscriptions?sku=$productId&package=${context.packageName}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.android.vending")
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            context.startActivity(intent.apply { setPackage(null) })
        }
    }
}
