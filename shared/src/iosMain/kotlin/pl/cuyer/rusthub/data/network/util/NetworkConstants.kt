package pl.cuyer.rusthub.data.network.util

import kotlin.native.Platform

actual object NetworkConstants {
    actual val BASE_URL: String
        get() = if (Platform.isDebugBinary) {
            "https://dev.api.rusthub.me/"
        } else {
            "https://api.rusthub.me/"
        }
}

