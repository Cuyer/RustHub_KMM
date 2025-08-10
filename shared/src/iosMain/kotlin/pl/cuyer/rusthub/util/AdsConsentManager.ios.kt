package pl.cuyer.rusthub.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AdSupport.ASIdentifierManager
import platform.AppTrackingTransparency.ATTrackingManager
import platform.AppTrackingTransparency.ATTrackingManagerAuthorizationStatusAuthorized
import platform.Foundation.NSOperatingSystemVersion
import platform.Foundation.NSProcessInfo

actual class AdsConsentManager {

    actual val canRequestAds: Boolean
        get() = if (
            ATTrackingManager.trackingAuthorizationStatus() ==
                ATTrackingManagerAuthorizationStatusAuthorized
        ) {
            true
        } else {
            ASIdentifierManager.sharedManager().isAdvertisingTrackingEnabled()
        }

    actual val isPrivacyOptionsRequired: Boolean = false

    @OptIn(ExperimentalForeignApi::class)
    actual fun gatherConsent(activity: Any, onComplete: (String?) -> Unit) {
        val info = NSProcessInfo.processInfo
        val version = NSOperatingSystemVersion(majorVersion = 14, minorVersion = 0, patchVersion = 0)
        if (info.isOperatingSystemAtLeastVersion(version)) {
            ATTrackingManager.requestTrackingAuthorizationWithCompletionHandler { _: UInt ->
                onComplete(null)
            }
        } else {
            onComplete(null)
        }
    }

    actual fun showPrivacyOptionsForm(activity: Any, onDismiss: (String?) -> Unit) {
        onDismiss(null)
    }
}

