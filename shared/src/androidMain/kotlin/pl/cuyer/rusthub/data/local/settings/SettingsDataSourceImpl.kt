package pl.cuyer.rusthub.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource

class SettingsDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsDataSource {

    private val themeKey = stringPreferencesKey("theme")
    private val languageKey = stringPreferencesKey("language")

    override fun getSettings(): Flow<Settings?> {
        return dataStore.data.map { prefs ->
            val theme = prefs[themeKey]?.let { Theme.valueOf(it) } ?: Theme.SYSTEM
            val language = prefs[languageKey]?.let { Language.valueOf(it) } ?: Language.ENGLISH
            Settings(theme, language)
        }
    }

    override suspend fun upsertSettings(settings: Settings) {
        dataStore.edit { prefs ->
            prefs[themeKey] = settings.theme.name
            prefs[languageKey] = settings.language.name
        }
    }
}
