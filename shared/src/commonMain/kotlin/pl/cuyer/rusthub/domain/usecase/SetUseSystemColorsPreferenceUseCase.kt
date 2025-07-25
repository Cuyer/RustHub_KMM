package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class SetUseSystemColorsPreferenceUseCase(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(useSystemColors: Boolean) {
        repository.setUseSystemColors(useSystemColors)
    }
}
