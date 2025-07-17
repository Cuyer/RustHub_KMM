package pl.cuyer.rusthub.data.local.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource

class SettingsDataSourceImpl : SettingsDataSource {

    private val state = MutableStateFlow<Settings?>(null)

    override fun getSettings(): Flow<Settings?> = state

    override suspend fun upsertSettings(settings: Settings) {
        state.value = settings
    }
}
