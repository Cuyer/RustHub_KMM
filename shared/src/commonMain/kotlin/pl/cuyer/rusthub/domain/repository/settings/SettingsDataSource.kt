package pl.cuyer.rusthub.domain.repository.settings

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.Language

interface SettingsDataSource {
    fun getSettings(): Flow<Settings?>
    suspend fun upsertSettings(settings: Settings)

    suspend fun getTheme(): Theme?
    suspend fun getLanguage(): Language?
    suspend fun setTheme(theme: Theme)
    suspend fun setLanguage(language: Language)
}
