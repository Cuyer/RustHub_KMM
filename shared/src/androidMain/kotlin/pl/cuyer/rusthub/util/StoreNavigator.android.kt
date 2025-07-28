package pl.cuyer.rusthub.util

import android.content.Context
import android.content.Intent
import android.net.Uri

actual class StoreNavigator(private val context: Context) {
    actual fun openStore() {
        val uri = Uri.parse("market://details?id=${context.packageName}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(webIntent)
        }
    }

    actual fun openSubscriptionManagement(productId: String) {
        val uri = Uri.parse(
            "https://play.google.com/store/account/subscriptions?sku=$productId&package=${context.packageName}"
        )
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            context.startActivity(intent.apply { setPackage(null) })
        }
    }
}
