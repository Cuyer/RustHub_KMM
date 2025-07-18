package pl.cuyer.rusthub.data.local.user

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.UserPreferences
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dataSource: RustHubPreferencesDataSource
) : UserPreferencesRepository {
    override val userPreferences: Flow<UserPreferences> = dataSource.preferences

    override suspend fun setThemeConfig(theme: Theme) {
        dataSource.setThemeConfig(theme)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        dataSource.setDynamicColorPreference(useDynamicColor)
    }
}
