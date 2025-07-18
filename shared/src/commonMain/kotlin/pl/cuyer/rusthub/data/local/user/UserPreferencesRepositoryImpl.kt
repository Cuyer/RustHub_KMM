package pl.cuyer.rusthub.data.local.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.UserPreferences
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class UserPreferencesRepositoryImpl : UserPreferencesRepository {
    override val userPreferences: Flow<UserPreferences> = flowOf(UserPreferences())

    override suspend fun setThemeConfig(theme: Theme) {
        // no-op
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        // no-op
    }
}
