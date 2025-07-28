package pl.cuyer.rusthub.util

import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class StoreNavigator {
    actual fun openStore() {
        val bundleId = NSBundle.mainBundle.bundleIdentifier ?: return
        val url = NSURL.URLWithString("itms-apps://itunes.apple.com/app/" + bundleId)
        if (url != null) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    actual fun openSubscriptionManagement(productId: String) {
        val url = NSURL.URLWithString("https://apps.apple.com/account/subscriptions")
        if (url != null) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}
