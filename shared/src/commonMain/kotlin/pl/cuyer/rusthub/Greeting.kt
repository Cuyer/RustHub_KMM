package pl.cuyer.rusthub

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.localized
import dev.icerock.moko.resources.format
import pl.cuyer.rusthub.SharedRes

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return SharedRes.strings.hello_username.format(platform.name).localized()
    }
}