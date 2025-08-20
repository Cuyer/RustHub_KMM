package pl.cuyer.rusthub.util

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
actual object BuildType {
    actual val isDebug: Boolean
        get() = Platform.isDebugBinary
}
