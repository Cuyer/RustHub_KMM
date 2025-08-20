package pl.cuyer.rusthub.util

expect class RemoteConfig() {
    fun getBoolean(key: String): Boolean
}

object RemoteConfigKeys {
    const val GOOGLE_AUTH_ENABLED = "GOOGLE_AUTH_ENABLED"
    const val FEATURE_SUBSCRIPTION_ENABLED = "FEATURE_SUBSCRIPTION_ENABLED"
}
