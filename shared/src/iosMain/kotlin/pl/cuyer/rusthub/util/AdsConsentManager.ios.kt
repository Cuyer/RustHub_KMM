package pl.cuyer.rusthub.util

actual class AdsConsentManager {
    actual val canRequestAds: Boolean = false
    actual val isPrivacyOptionsRequired: Boolean = false
    actual fun gatherConsent(activity: Any, onComplete: (String?) -> Unit) { onComplete(null) }
    actual fun showPrivacyOptionsForm(activity: Any, onDismiss: (String?) -> Unit) { onDismiss(null) }
}
