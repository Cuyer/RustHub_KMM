package pl.cuyer.rusthub.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import pl.cuyer.rusthub.domain.model.Language
import java.util.Locale

actual fun updateAppLanguage(language: Language) {
    val locale = when (language) {
        Language.ENGLISH -> Locale.forLanguageTag("en")
        Language.POLISH -> Locale.forLanguageTag("pl")
    }
    Locale.setDefault(locale)
    val locales = LocaleListCompat.forLanguageTags(locale.toLanguageTag())
    AppCompatDelegate.setApplicationLocales(locales)
}