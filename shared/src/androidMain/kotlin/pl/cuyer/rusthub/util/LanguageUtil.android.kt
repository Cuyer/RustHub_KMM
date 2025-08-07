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
    val locales = LocaleListCompat.forLanguageTags(tag)
    AppCompatDelegate.setApplicationLocales(locales)
}

actual fun getCurrentAppLanguage(): Language {
    val locales = AppCompatDelegate.getApplicationLocales()
    val code = if (!locales.isEmpty) locales[0]!!.language else Locale.getDefault().language
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
