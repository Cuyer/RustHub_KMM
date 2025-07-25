package pl.cuyer.rusthub.util

import android.content.Context
import dev.icerock.moko.resources.StringResource

actual class StringProvider(private val context: Context) {
    actual fun get(res: StringResource, vararg args: Any): String {
        return if (args.isEmpty()) {
            res.getString(context)
        } else {
            context.getString(res.resourceId, *args)
        }
    }
}
