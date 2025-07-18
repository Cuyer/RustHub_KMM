package pl.cuyer.rusthub.domain.repository.user

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.DarkThemeConfig
import pl.cuyer.rusthub.domain.model.UserData

interface UserPreferencesRepository {
    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>
    /**
     * Sets the desired theme config.
     */
    suspend fun setThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)
}
