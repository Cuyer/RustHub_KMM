package pl.cuyer.rusthub.common

import kotlin.native.Platform

actual object Urls {
    actual val PRIVACY_POLICY_URL: String
        get() = if (Platform.isDebugBinary) {
            "https://dev.rusthub.me/privacy"
        } else {
            "https://rusthub.me/privacy"
        }

    actual val TERMS_URL: String
        get() = if (Platform.isDebugBinary) {
            "https://dev.rusthub.me/terms"
        } else {
            "https://rusthub.me/terms"
        }
}

