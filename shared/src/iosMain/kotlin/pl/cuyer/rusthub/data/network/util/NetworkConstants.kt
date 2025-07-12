package pl.cuyer.rusthub.data.network.util

import kotlin.experimental.ExperimentalNativeApi

actual object NetworkConstants {
    @OptIn(ExperimentalNativeApi::class)
    actual val BASE_URL: String
        get() = if (Platform.isDebugBinary) {
            "https://api.dev.rusthub.me/"
        } else {
            "https://api.rusthub.me/"
        }
}

