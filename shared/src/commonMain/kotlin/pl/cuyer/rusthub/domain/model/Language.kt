package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

enum class Language {
    ENGLISH,
    POLISH;

    companion object {
        fun fromDisplayName(displayName: String, stringProvider: StringProvider): Language? {
            return entries.firstOrNull { it.displayName(stringProvider) == displayName }
        }
    }
}

fun Language.displayName(stringProvider: StringProvider): String =
    when (this) {
        Language.ENGLISH -> stringProvider.get(SharedRes.strings.english)
        Language.POLISH -> stringProvider.get(SharedRes.strings.polish)
    }
