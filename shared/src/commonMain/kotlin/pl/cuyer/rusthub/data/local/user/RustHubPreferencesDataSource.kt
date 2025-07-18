package pl.cuyer.rusthub.data.local.user

import androidx.datastore.core.DataStore
import pl.cuyer.rusthub.ThemeProto
import pl.cuyer.rusthub.UserPreferencesProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.UserPreferences

class RustHubPreferencesDataSource(
    private val userPreferences: DataStore<UserPreferencesProto>
) {
    val preferences: Flow<UserPreferences> = userPreferences.data.map {
        UserPreferences(
            themeConfig = when (it.themeConfig) {
                ThemeProto.THEME_LIGHT -> Theme.LIGHT
                ThemeProto.THEME_DARK -> Theme.DARK
                else -> Theme.SYSTEM
            },
            useDynamicColor = it.useDynamicColor,
        )
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy(useDynamicColor = useDynamicColor)
        }
    }

    suspend fun setTheme(theme: Theme) {
        userPreferences.updateData {
            it.copy(
                themeConfig = when (theme) {
                    Theme.SYSTEM -> ThemeProto.THEME_SYSTEM
                    Theme.LIGHT -> ThemeProto.THEME_LIGHT
                    Theme.DARK -> ThemeProto.THEME_DARK
                }
            )
        }
    }
}
