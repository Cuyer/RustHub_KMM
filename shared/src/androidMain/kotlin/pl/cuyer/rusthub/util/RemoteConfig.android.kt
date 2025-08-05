package pl.cuyer.rusthub.util

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

actual class RemoteConfig actual constructor() {
    private val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        setConfigSettingsAsync(settings)
        setDefaultsAsync(
            mapOf(
                RemoteConfigKeys.GOOGLE_AUTH_ENABLED to true,
                RemoteConfigKeys.FEATURE_SUBSCRIPTION_ENABLED to true
            )
        )
        fetchAndActivate()
    }

    actual fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)
}
