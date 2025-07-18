package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class SetThemeConfigUseCase(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(theme: Theme) {
        repository.setThemeConfig(theme)
    }
}
