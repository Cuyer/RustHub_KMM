package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class SetDynamicColorPreferenceUseCase(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(useDynamicColor: Boolean) {
        repository.setDynamicColorPreference(useDynamicColor)
    }
}
