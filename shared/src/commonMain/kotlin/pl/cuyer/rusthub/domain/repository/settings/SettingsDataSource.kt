package pl.cuyer.rusthub.domain.repository.settings

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Settings

interface SettingsDataSource {
    fun getSettings(): Flow<Settings?>
    suspend fun upsertSettings(settings: Settings)
}
