package pl.cuyer.rusthub.util

import platform.Foundation.NSBundle

actual object AppInfo {
    actual val versionName: String =
        NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: ""
}

