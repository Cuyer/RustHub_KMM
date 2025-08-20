package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import androidx.compose.runtime.Immutable

@Immutable
enum class Language {
    ENGLISH,
    POLISH,
    GERMAN,
    FRENCH,
    RUSSIAN,
    PORTUGUESE,
    SPANISH,
    UKRAINIAN;

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
        Language.GERMAN -> stringProvider.get(SharedRes.strings.german)
        Language.FRENCH -> stringProvider.get(SharedRes.strings.french)
        Language.RUSSIAN -> stringProvider.get(SharedRes.strings.russian)
        Language.PORTUGUESE -> stringProvider.get(SharedRes.strings.portuguese)
        Language.SPANISH -> stringProvider.get(SharedRes.strings.spanish)
        Language.UKRAINIAN -> stringProvider.get(SharedRes.strings.ukrainian)
    }
