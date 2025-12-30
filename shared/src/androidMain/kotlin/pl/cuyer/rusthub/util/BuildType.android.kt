package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.SharedBuildConfig

actual object BuildType {
    actual val isDebug: Boolean = SharedBuildConfig.IS_DEBUG_BUILD
}
