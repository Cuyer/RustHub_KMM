package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import androidx.compose.runtime.Immutable

@Immutable
enum class Theme {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromDisplayName(displayName: String, stringProvider: StringProvider): Theme? =
            entries.firstOrNull { it.displayName(stringProvider) == displayName }
    }
}

fun Theme.displayName(stringProvider: StringProvider): String =
    when (this) {
        Theme.LIGHT -> stringProvider.get(SharedRes.strings.light)
        Theme.DARK -> stringProvider.get(SharedRes.strings.dark)
        Theme.SYSTEM -> stringProvider.get(SharedRes.strings.system)
    }
