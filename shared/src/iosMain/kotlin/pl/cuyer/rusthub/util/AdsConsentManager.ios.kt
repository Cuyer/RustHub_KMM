package pl.cuyer.rusthub.util

import apptrackingtransparency.ATTrackingManager
import platform.AdSupport.ASIdentifierManager

actual class AdsConsentManager {

    actual val canRequestAds: Boolean
        get() =
            if (ATTrackingManager.trackingAuthorizationStatus() ==
                ATTrackingManagerAuthorizationStatusAuthorized)
                true else ASIdentifierManager.sharedManager().isAdvertisingTrackingEnabled()

    actual val isPrivacyOptionsRequired: Boolean = false

    actual fun gatherConsent(activity: Any, onComplete: (String?) -> Unit) {
        if (platform.Foundation.NSProcessInfo.processInfo.isOperatingSystemAtLeastVersion(
                platform.Foundation.NSOperatingSystemVersion(14, 0, 0)
            )
        ) {
            ATTrackingManager.requestTrackingAuthorizationWithCompletionHandler { _ ->
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

