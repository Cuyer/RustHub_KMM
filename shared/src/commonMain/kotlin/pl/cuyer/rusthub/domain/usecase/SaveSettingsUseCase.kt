package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource

class SaveSettingsUseCase(
    private val dataSource: SettingsDataSource
) {
    suspend operator fun invoke(settings: Settings) {
        dataSource.upsertSettings(settings)
    }
}
