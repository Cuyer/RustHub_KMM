package pl.cuyer.rusthub.data.local.settings

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource

class SettingsDataSourceImpl : SettingsDataSource {

    private val TAG = "SettingsDataSource"
    override fun getTheme(): Theme {
        val mode = AppCompatDelegate.getDefaultNightMode()
        val theme = when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO -> Theme.LIGHT
            AppCompatDelegate.MODE_NIGHT_YES -> Theme.DARK
            else -> Theme.SYSTEM
        }
        Log.d(TAG, "getTheme: mode=$mode resolvedTheme=$theme")
        return theme
    }

    override fun getLanguage(): Language {
        val locales = AppCompatDelegate.getApplicationLocales()
        val tag = if (!locales.isEmpty) locales[0]!!.language else null
        val language = when (tag) {
            "pl" -> Language.POLISH
            "de" -> Language.GERMAN
            "fr" -> Language.FRENCH
            "ru" -> Language.RUSSIAN
            else -> Language.ENGLISH
        }
        Log.d(TAG, "getLanguage: tag=$tag resolvedLanguage=$language")
        return language
    }

    override fun setTheme(theme: Theme) {
        val mode = when (theme) {
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        Log.d(TAG, "setTheme: theme=$theme resolvedMode=$mode")
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun setLanguage(language: Language) {
        val localeTag = when (language) {
            Language.ENGLISH -> "en"
            Language.POLISH -> "pl"
            Language.GERMAN -> "de"
            Language.FRENCH -> "fr"
            Language.RUSSIAN -> "ru"
        }
        val locales = LocaleListCompat.forLanguageTags(localeTag)
        Log.d(TAG, "setLanguage: language=$language localeTag=$localeTag")
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
