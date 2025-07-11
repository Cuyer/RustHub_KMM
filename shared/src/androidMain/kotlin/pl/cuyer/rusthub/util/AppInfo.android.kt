package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.SharedBuildConfig

actual object AppInfo {
    actual val versionName: String = SharedBuildConfig.VERSION_NAME
}

