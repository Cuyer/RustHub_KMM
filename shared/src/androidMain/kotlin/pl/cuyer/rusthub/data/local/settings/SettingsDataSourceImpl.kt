package pl.cuyer.rusthub.data.local.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource

class SettingsDataSourceImpl : SettingsDataSource {

    override fun getSettings(): Flow<Settings?> {
        val theme = getTheme() ?: Theme.SYSTEM
        val language = getLanguage() ?: Language.ENGLISH
        return flowOf(Settings(theme, language))
    }

    override suspend fun upsertSettings(settings: Settings) {
        setTheme(settings.theme)
        setLanguage(settings.language)
    }

    override suspend fun getTheme(): Theme? {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> Theme.LIGHT
            AppCompatDelegate.MODE_NIGHT_YES -> Theme.DARK
            else -> Theme.SYSTEM
        }
    }

    override suspend fun getLanguage(): Language? {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (!locales.isEmpty) {
            val tag = locales[0]!!.language
            return when (tag) {
                "pl" -> Language.POLISH
                else -> Language.ENGLISH
            }
        }
        return Language.ENGLISH
    }

    override suspend fun setTheme(theme: Theme) {
        val mode = when (theme) {
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override suspend fun setLanguage(language: Language) {
        val locale = when (language) {
            Language.ENGLISH -> "en"
            Language.POLISH -> "pl"
        }
        val locales = LocaleListCompat.forLanguageTags(locale)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
