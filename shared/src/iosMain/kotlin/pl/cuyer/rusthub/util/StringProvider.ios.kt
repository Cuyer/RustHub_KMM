package pl.cuyer.rusthub.util

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

actual class StringProvider {
    actual fun get(res: StringResource, vararg args: Any): String {
        return if (args.isEmpty()) {
            res.desc().localized()
        } else {
            res.format(*args).localized()
        }
    }
}
