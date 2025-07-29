package pl.cuyer.rusthub.util

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

actual class AdsConsentManager private constructor(context: Context) {
    private val consentInformation = UserMessagingPlatform.getConsentInformation(context)

    actual val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    actual val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    actual fun gatherConsent(activity: Any, onComplete: (String?) -> Unit) {
        val act = activity as? Activity ?: return
        val debugSettings = ConsentDebugSettings.Builder(act)
            .addTestDeviceHashedId(TEST_DEVICE_HASHED_ID)
            .build()
        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        consentInformation.requestConsentInfoUpdate(
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

        fun getInstance(context: Context): AdsConsentManager =
            instance ?: synchronized(this) {
                instance ?: AdsConsentManager(context).also { instance = it }
            }
    }
}
