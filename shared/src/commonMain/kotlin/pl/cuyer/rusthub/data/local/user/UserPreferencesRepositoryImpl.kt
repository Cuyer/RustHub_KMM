package pl.cuyer.rusthub.data.local.user

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.UserPreferences
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dataSource: RustHubPreferencesDataSource
) : UserPreferencesRepository {
    override val userPreferences: Flow<UserPreferences> = dataSource.preferences

    override suspend fun setThemeConfig(theme: Theme) {
        dataSource.setTheme(theme)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        dataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setUseSystemColors(useSystemColors: Boolean) {
        dataSource.setUseSystemColors(useSystemColors)
    }
}
