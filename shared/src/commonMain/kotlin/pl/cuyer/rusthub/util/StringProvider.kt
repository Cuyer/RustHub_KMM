package pl.cuyer.rusthub.util

import dev.icerock.moko.resources.StringResource

expect class StringProvider {
    fun get(res: StringResource, vararg args: Any): String
}
