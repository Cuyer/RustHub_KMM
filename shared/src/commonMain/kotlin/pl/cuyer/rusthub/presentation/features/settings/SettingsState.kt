package pl.cuyer.rusthub.presentation.features.settings

import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme

data class SettingsState(
    val theme: Theme = Theme.SYSTEM,
    val language: Language = Language.ENGLISH,
    val username: String? = null
)
