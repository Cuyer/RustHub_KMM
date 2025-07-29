package pl.cuyer.rusthub.util

expect class AdsConsentManager {
    val canRequestAds: Boolean
    val isPrivacyOptionsRequired: Boolean
    fun gatherConsent(activity: Any, onComplete: (String?) -> Unit)
    fun showPrivacyOptionsForm(activity: Any, onDismiss: (String?) -> Unit)
}
