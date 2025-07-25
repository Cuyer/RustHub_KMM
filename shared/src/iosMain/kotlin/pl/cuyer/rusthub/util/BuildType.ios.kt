package pl.cuyer.rusthub.util

import kotlin.native.Platform

actual object BuildType {
    actual val isDebug: Boolean
        get() = Platform.isDebugBinary
}
