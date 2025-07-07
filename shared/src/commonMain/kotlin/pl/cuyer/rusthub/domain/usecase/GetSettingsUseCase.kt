package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource

class GetSettingsUseCase(
    private val dataSource: SettingsDataSource
) {
    operator fun invoke(): Flow<Settings?> {
        return dataSource.getSettings()
    }
}
