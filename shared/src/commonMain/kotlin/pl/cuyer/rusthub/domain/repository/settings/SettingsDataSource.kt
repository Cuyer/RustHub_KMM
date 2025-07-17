package pl.cuyer.rusthub.domain.repository.settings

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.Language

interface SettingsDataSource {
    fun getTheme(): Theme
    fun getLanguage(): Language
    fun setTheme(theme: Theme)
    fun setLanguage(language: Language)

    fun applySettings()
}
