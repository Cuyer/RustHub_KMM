package pl.cuyer.rusthub.util

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.os.LocaleListCompat
import pl.cuyer.rusthub.domain.model.Language
import java.util.Locale

actual fun updateAppLanguage(language: Language, activity: Any?) {
    val locale = when (language) {
        Language.ENGLISH -> Locale.forLanguageTag("en")
        Language.POLISH -> Locale.forLanguageTag("pl")
    }
    Locale.setDefault(locale)
    val locales = LocaleListCompat.forLanguageTags(locale.toLanguageTag())
    AppCompatDelegate.setApplicationLocales(locales)
    val act = activity as? Activity
    act?.let { ActivityCompat.recreate(it) }
}

actual fun getCurrentAppLanguage(): Language {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (!locales.isEmpty) {
        val languageTag = locales[0]!!.language
        return when (languageTag) {
            "pl" -> Language.POLISH
            else -> Language.ENGLISH
        }
    }
    return Language.ENGLISH
}
