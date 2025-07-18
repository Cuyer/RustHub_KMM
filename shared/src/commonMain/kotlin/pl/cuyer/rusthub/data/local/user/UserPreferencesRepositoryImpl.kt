package pl.cuyer.rusthub.data.local.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pl.cuyer.rusthub.domain.model.DarkThemeConfig
import pl.cuyer.rusthub.domain.model.UserData
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

class UserPreferencesRepositoryImpl : UserPreferencesRepository {
    override val userData: Flow<UserData> = flowOf(UserData())

    override suspend fun setThemeConfig(darkThemeConfig: DarkThemeConfig) {
        // no-op
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        // no-op
    }
}
