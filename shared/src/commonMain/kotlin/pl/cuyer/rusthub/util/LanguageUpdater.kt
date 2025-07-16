package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.Language

expect fun updateAppLanguage(language: Language, activity: Any? = null)
expect fun getCurrentAppLanguage(): Language
