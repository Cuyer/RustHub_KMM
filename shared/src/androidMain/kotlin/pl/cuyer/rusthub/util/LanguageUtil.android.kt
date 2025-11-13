package pl.cuyer.rusthub.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import pl.cuyer.rusthub.domain.model.Language
import java.util.Locale

actual fun updateAppLanguage(language: Language) {
    val tag = when (language) {
        Language.ENGLISH -> "en"
        Language.POLISH -> "pl"
        Language.GERMAN -> "de"
        Language.FRENCH -> "fr"
        Language.RUSSIAN -> "ru"
        Language.PORTUGUESE -> "pt"
        Language.SPANISH -> "es"
        Language.UKRAINIAN -> "uk"
    }
    val locale = Locale.forLanguageTag(tag)
    if (locale.language.isNotEmpty()) {
        val locales = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}

actual fun getCurrentAppLanguage(): Language {
    val locale = runCatching {
        AppCompatDelegate.getApplicationLocales().takeIf { !it.isEmpty }?.get(0)
    }.getOrNull()
    val code = (locale ?: Locale.getDefault()).language
    return when (code) {
        "pl" -> Language.POLISH
        "de" -> Language.GERMAN
        "fr" -> Language.FRENCH
        "ru" -> Language.RUSSIAN
        "pt" -> Language.PORTUGUESE
        "es" -> Language.SPANISH
        "uk" -> Language.UKRAINIAN
        else -> Language.ENGLISH
    }
}
