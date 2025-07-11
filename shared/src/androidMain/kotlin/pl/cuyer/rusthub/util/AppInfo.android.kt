package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.BuildConfig

actual object AppInfo {
    actual val versionName: String = BuildConfig.VERSION_NAME
}

