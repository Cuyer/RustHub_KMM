package pl.cuyer.rusthub.util

import android.app.Activity
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

actual class AdsConsentManager private constructor(
    private val activityProvider: ActivityProvider
) {
    private val consentInformation: ConsentInformation?
        get() = activityProvider.currentActivity()?.let {
            UserMessagingPlatform.getConsentInformation(it)
        }

    actual val canRequestAds: Boolean
        get() = consentInformation?.canRequestAds() ?: false

    actual val isPrivacyOptionsRequired: Boolean
        get() = consentInformation?.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    actual fun gatherConsent(activity: Any, onComplete: (String?) -> Unit) {
        val act = activity as? Activity ?: return
        val paramsBuilder = ConsentRequestParameters.Builder()
        if (BuildType.isDebug) {
            val debugSettings = ConsentDebugSettings.Builder(act)
                .addTestDeviceHashedId(TEST_DEVICE_HASHED_ID)
                .build()
            paramsBuilder.setConsentDebugSettings(debugSettings)
        }
        val params = paramsBuilder.build()

        UserMessagingPlatform.getConsentInformation(act).requestConsentInfoUpdate(
            act,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(act) { formError ->
                    onComplete(formError?.message)
                }
            },
            { requestError ->
                onComplete(requestError.message)
            }
        )
    }

    actual fun showPrivacyOptionsForm(activity: Any, onDismiss: (String?) -> Unit) {
        val act = activity as? Activity ?: return
        UserMessagingPlatform.showPrivacyOptionsForm(act) { formError ->
            onDismiss(formError?.message)
        }
    }

    companion object {
        private var instance: AdsConsentManager? = null
        private const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"

        fun getInstance(activityProvider: ActivityProvider): AdsConsentManager =
            instance ?: synchronized(this) {
                instance ?: AdsConsentManager(activityProvider).also { instance = it }
            }
    }
}
