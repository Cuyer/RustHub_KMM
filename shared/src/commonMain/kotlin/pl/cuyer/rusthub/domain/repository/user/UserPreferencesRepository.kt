package pl.cuyer.rusthub.domain.repository.user

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.UserPreferences

interface UserPreferencesRepository {
    /**
     * Stream of [UserPreferences]
     */
    val userPreferences: Flow<UserPreferences>
    /**
     * Sets the desired theme config.
     */
    suspend fun setThemeConfig(theme: Theme)

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)
}
