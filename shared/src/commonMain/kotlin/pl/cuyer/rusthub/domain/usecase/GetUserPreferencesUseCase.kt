package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.UserPreferences
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class GetUserPreferencesUseCase(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<UserPreferences> = repository.userPreferences
}
