package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.BuildConfig

actual object BuildType {
    actual val isDebug: Boolean = BuildConfig.DEBUG
}
