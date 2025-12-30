package pl.cuyer.rusthub.common

import pl.cuyer.rusthub.SharedBuildConfig

actual object Urls {
    actual val PRIVACY_POLICY_URL: String = SharedBuildConfig.PRIVACY_POLICY_URL
    actual val TERMS_URL: String = SharedBuildConfig.TERMS_URL
}

